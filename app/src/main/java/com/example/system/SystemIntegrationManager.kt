package com.example.system

import android.app.Activity
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.app.NotificationManagerCompat
import com.example.model.AppCategory
import com.example.model.AppItem
import com.example.model.RealSystemTelemetry
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/**
 * Manages legitimate Android hardware & system API integrations with lifecycle-aware registration,
 * zero background polling, and explicit permission checks.
 */
class SystemIntegrationManager(private val context: Context) {

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    private val connectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    private val bluetoothManager =
        appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private val _telemetry = MutableStateFlow( RealSystemTelemetry() )
    val telemetry: StateFlow<RealSystemTelemetry> = _telemetry.asStateFlow()

    private var torchCameraId: String? = null
    private var isObserving = false

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == torchCameraId) {
                _telemetry.update {
                    it.copy(flashlightAvailable = true, flashlightActive = enabled)
                }
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (cameraId == torchCameraId) {
                _telemetry.update { it.copy(flashlightActive = false) }
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent == null) return
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 275)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
            val pct = if (level >= 0 && scale > 0) {
                ((level.toFloat() / scale.toFloat()) * 100f).roundToInt().coerceIn(0, 100)
            } else {
                88
            }
            _telemetry.update {
                it.copy(
                    batteryPercent = pct,
                    isCharging = isCharging,
                    batteryTempCelsius = tempTenths / 10f
                )
            }
        }
    }

    init {
        discoverTorchUnit()
        refreshSystemSnapshot()
    }

    private fun discoverTorchUnit() {
        runCatching {
            val ids = cameraManager?.cameraIdList ?: emptyArray()
            for (id in ids) {
                val chars = cameraManager?.getCameraCharacteristics(id)
                val hasFlash = chars?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                if (hasFlash) {
                    torchCameraId = id
                    break
                }
            }
        }
        _telemetry.update { it.copy(flashlightAvailable = torchCameraId != null) }
    }

    fun startObserving() {
        if (isObserving) return
        isObserving = true
        runCatching {
            appContext.registerReceiver(
                batteryReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
        }
        runCatching {
            if (torchCameraId != null) {
                cameraManager?.registerTorchCallback(
                    torchCallback,
                    Handler(Looper.getMainLooper())
                )
            }
        }
        refreshSystemSnapshot()
    }

    fun stopObserving() {
        if (!isObserving) return
        isObserving = false
        runCatching { appContext.unregisterReceiver(batteryReceiver) }
        runCatching { cameraManager?.unregisterTorchCallback(torchCallback) }
    }

    fun refreshSystemSnapshot() {
        val mediaVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 8
        val mediaMax = (audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15).coerceAtLeast(1)
        val ringVol = audioManager?.getStreamVolume(AudioManager.STREAM_RING) ?: 5
        val ringMax = (audioManager?.getStreamMaxVolume(AudioManager.STREAM_RING) ?: 7).coerceAtLeast(1)
        val musicActive = audioManager?.isMusicActive == true

        val rawBrightness = runCatching {
            Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 180)
        }.getOrDefault(180)
        val brightnessPct = ((rawBrightness.coerceIn(0, 255) / 255f) * 100f).roundToInt()

        val canWriteSettings = Settings.System.canWrite(appContext)
        val autoRotate = runCatching {
            Settings.System.getInt(appContext.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1) == 1
        }.getOrDefault(true)

        val wifiEnabled = runCatching { wifiManager?.isWifiEnabled ?: true }.getOrDefault(true)
        var netConnected = false
        var transportLabel = "Offline"
        runCatching {
            val activeNet = connectivityManager?.activeNetwork
            val caps = if (activeNet != null) connectivityManager?.getNetworkCapabilities(activeNet) else null
            if (caps != null) {
                netConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                transportLabel = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Active"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "5G / Cellular"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    else -> "Connected"
                }
            }
        }

        val btAdapter = runCatching { bluetoothManager?.adapter }.getOrNull()
        val btSupported = btAdapter != null
        val btEnabled = runCatching { btAdapter?.isEnabled == true }.getOrDefault(false)

        val canAccessDnd = notificationManager?.isNotificationPolicyAccessGranted == true
        val interruptionFilter = notificationManager?.currentInterruptionFilter
            ?: NotificationManager.INTERRUPTION_FILTER_ALL
        val dndActive = interruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL

        val notifListenerEnabled = runCatching {
            NotificationManagerCompat.getEnabledListenerPackages(appContext)
                .contains(appContext.packageName)
        }.getOrDefault(false)

        _telemetry.update { current ->
            current.copy(
                wifiEnabled = wifiEnabled,
                networkConnected = netConnected,
                networkTransportLabel = transportLabel,
                bluetoothEnabled = btEnabled,
                bluetoothSupported = btSupported,
                mediaVolume = mediaVol,
                mediaMaxVolume = mediaMax,
                ringVolume = ringVol,
                ringMaxVolume = ringMax,
                brightnessPercent = if (canWriteSettings) brightnessPct else current.brightnessPercent,
                canWriteSystemSettings = canWriteSettings,
                canAccessDndPolicy = canAccessDnd,
                dndActive = dndActive,
                autoRotateEnabled = autoRotate,
                notificationListenerGranted = notifListenerEnabled,
                systemMediaPlaying = musicActive
            )
        }
    }

    fun setMediaVolume(volume: Int) {
        val max = _telemetry.value.mediaMaxVolume
        val clamped = volume.coerceIn(0, max)
        runCatching {
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, clamped, 0)
        }
        _telemetry.update { it.copy(mediaVolume = clamped) }
    }

    fun setRingVolume(volume: Int) {
        val max = _telemetry.value.ringMaxVolume
        val clamped = volume.coerceIn(0, max)
        runCatching {
            audioManager?.setStreamVolume(AudioManager.STREAM_RING, clamped, 0)
        }
        _telemetry.update { it.copy(ringVolume = clamped) }
    }

    /**
     * Adjusts window brightness immediately and writes to Settings.System if authorized.
     */
    fun setBrightnessPercent(activity: Activity?, percent: Int) {
        val clamped = percent.coerceIn(5, 100)
        // Always apply to current window immediately so user sees real brightness change
        activity?.window?.let { win ->
            val params = win.attributes
            params.screenBrightness = clamped / 100f
            win.attributes = params
        }
        val canWrite = Settings.System.canWrite(appContext)
        if (canWrite) {
            val sysVal = ((clamped / 100f) * 255f).roundToInt().coerceIn(10, 255)
            runCatching {
                Settings.System.putInt(
                    appContext.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    sysVal
                )
            }
        }
        _telemetry.update {
            it.copy(brightnessPercent = clamped, canWriteSystemSettings = canWrite)
        }
    }

    /**
     * Toggles hardware torch if available, or toggles Screen Torch fallback if device has no flash unit.
     */
    fun toggleFlashlight(): String {
        val camId = torchCameraId
        if (camId != null && cameraManager != null) {
            val nextState = !_telemetry.value.flashlightActive
            return try {
                cameraManager.setTorchMode(camId, nextState)
                _telemetry.update { it.copy(flashlightActive = nextState) }
                if (nextState) "Hardware Flashlight On" else "Hardware Flashlight Off"
            } catch (e: Exception) {
                val fallbackNext = !_telemetry.value.screenTorchFallbackActive
                _telemetry.update { it.copy(screenTorchFallbackActive = fallbackNext) }
                if (fallbackNext) "Screen Torch Active (Camera Busy)" else "Screen Torch Off"
            }
        } else {
            val fallbackNext = !_telemetry.value.screenTorchFallbackActive
            _telemetry.update { it.copy(screenTorchFallbackActive = fallbackNext) }
            return if (fallbackNext) {
                "Screen Torch Active (No Hardware Flash Unit)"
            } else {
                "Screen Torch Off"
            }
        }
    }

    /**
     * Toggles Do Not Disturb if policy access is granted; otherwise opens the Android DND Policy Settings screen.
     */
    fun toggleOrRequestDnd(): Boolean {
        val nm = notificationManager ?: return false
        return if (nm.isNotificationPolicyAccessGranted) {
            val currentlyDnd = nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
            val nextFilter = if (currentlyDnd) {
                NotificationManager.INTERRUPTION_FILTER_ALL
            } else {
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
            }
            runCatching { nm.setInterruptionFilter(nextFilter) }
            refreshSystemSnapshot()
            true
        } else {
            openSystemSettings(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            false
        }
    }

    /**
     * Toggles system Auto-Rotate if Write Settings is granted; otherwise opens Display Settings.
     */
    fun toggleOrRequestAutoRotate(): Boolean {
        return if (Settings.System.canWrite(appContext)) {
            val next = !_telemetry.value.autoRotateEnabled
            runCatching {
                Settings.System.putInt(
                    appContext.contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION,
                    if (next) 1 else 0
                )
            }
            _telemetry.update { it.copy(autoRotateEnabled = next) }
            true
        } else {
            openSystemSettings(Settings.ACTION_DISPLAY_SETTINGS)
            false
        }
    }

    fun openWifiPanelOrSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val opened = openSystemSettings(Settings.Panel.ACTION_WIFI)
            if (!opened) openSystemSettings(Settings.ACTION_WIFI_SETTINGS)
        } else {
            openSystemSettings(Settings.ACTION_WIFI_SETTINGS)
        }
    }

    fun openBluetoothSettings() {
        openSystemSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
    }

    fun openWriteSettingsPermission() {
        runCatching {
            val intent = Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:${appContext.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        }.onFailure {
            openSystemSettings(Settings.ACTION_SETTINGS)
        }
    }

    fun openNotificationListenerSettings() {
        openSystemSettings(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }

    fun openDefaultHomeLauncherPicker() {
        if (!openSystemSettings(Settings.ACTION_HOME_SETTINGS)) {
            openSystemSettings(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        }
    }

    fun launchSystemCamera(): Boolean {
        return runCatching {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
            true
        }.getOrElse {
            openSystemSettings(Settings.ACTION_SETTINGS)
        }
    }

    fun openSystemSettings(action: String): Boolean {
        return runCatching {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
            true
        }.getOrDefault(false)
    }

    fun launchInstalledApp(packageName: String, activityName: String?): Boolean {
        return runCatching {
            val intent = if (!activityName.isNullOrBlank()) {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = ComponentName(packageName, activityName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
            } else {
                appContext.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            if (intent != null) {
                appContext.startActivity(intent)
                true
            } else {
                false
            }
        }.getOrDefault(false)
    }

    fun openAppDetailsSettings(packageName: String) {
        runCatching {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            appContext.startActivity(intent)
        }
    }

    suspend fun queryInstalledLauncherApps(): List<AppItem> = withContext(Dispatchers.IO) {
        runCatching {
            val pm = appContext.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(mainIntent, 0)
            }

            val palette = listOf(
                0xFF2E7CF6,
                0xFF30D158,
                0xFFFF9F0A,
                0xFF5E5CE6,
                0xFF32ADE6,
                0xFFFF453A
            )

            resolveInfos
                .asSequence()
                .mapNotNull { info ->
                    val activityInfo = info.activityInfo ?: return@mapNotNull null
                    val pkg = activityInfo.packageName ?: return@mapNotNull null
                    if (pkg == appContext.packageName) return@mapNotNull null
                    val label = info.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() }
                        ?: pkg.substringAfterLast('.')
                    val accent = palette[(pkg.hashCode() and Int.MAX_VALUE) % palette.size]
                    AppItem(
                        id = "pkg_${pkg}_${activityInfo.name}",
                        title = label,
                        subtitle = "Installed Android App",
                        category = AppCategory.INSTALLED_ANDROID,
                        packageName = pkg,
                        activityName = activityInfo.name,
                        isBuiltIn = false,
                        accentHex = accent,
                        iconKey = "android_app"
                    )
                }
                .distinctBy { it.packageName }
                .sortedBy { it.title.lowercase() }
                .take(36)
                .toList()
        }.getOrDefault(emptyList())
    }

    fun performHaptic(view: View, enabled: Boolean, isHeavy: Boolean = false) {
        if (!enabled) return
        runCatching {
            val feedbackConstant = if (isHeavy) {
                HapticFeedbackConstants.LONG_PRESS
            } else {
                HapticFeedbackConstants.CLOCK_TICK
            }
            view.performHapticFeedback(feedbackConstant)
        }
    }
}
