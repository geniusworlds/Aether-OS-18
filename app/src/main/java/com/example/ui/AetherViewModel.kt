package com.example.ui

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import com.example.data.aetherDataStore
import com.example.model.ActiveAudioSessionState
import com.example.model.ActiveTimerState
import com.example.model.AnimationIntensity
import com.example.model.AppCategory
import com.example.model.AppItem
import com.example.model.BuiltInMicroApp
import com.example.model.ClockStyle
import com.example.model.GridDensity
import com.example.model.IconSizeOption
import com.example.model.IconThemeOption
import com.example.model.LockShortcutAction
import com.example.model.NotificationItem
import com.example.model.RealSystemTelemetry
import com.example.model.ThemePreference
import com.example.model.UserSettings
import com.example.model.WallpaperPreset
import com.example.system.AetherNotificationListenerService
import com.example.system.SystemIntegrationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AetherViewModel(
    private val settingsRepository: SettingsRepository,
    val systemManager: SystemIntegrationManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings()
        )

    val telemetry: StateFlow<RealSystemTelemetry> = systemManager.telemetry
    val liveSystemNotifications: StateFlow<List<NotificationItem>> =
        AetherNotificationListenerService.liveNotifications

    private val builtInApps: List<AppItem> = listOf(
        AppItem(
            id = "builtin_weather",
            title = "Weather",
            subtitle = "Atmospheric & UV Index",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF32ADE6,
            iconKey = "weather"
        ),
        AppItem(
            id = "builtin_timer",
            title = "Chrono",
            subtitle = "Live Capsule Timer",
            category = AppCategory.PRODUCTIVITY,
            isBuiltIn = true,
            accentHex = 0xFFFF9F0A,
            iconKey = "timer"
        ),
        AppItem(
            id = "builtin_audio",
            title = "Pulse Audio",
            subtitle = "Spatial Sound & Media",
            category = AppCategory.MEDIA_WELLNESS,
            isBuiltIn = true,
            accentHex = 0xFFFF375F,
            iconKey = "audio"
        ),
        AppItem(
            id = "builtin_notes",
            title = "Studio Notes",
            subtitle = "Instant Scratchpad",
            category = AppCategory.PRODUCTIVITY,
            isBuiltIn = true,
            accentHex = 0xFFF59E0B,
            iconKey = "notes",
            badgeCount = 2
        ),
        AppItem(
            id = "builtin_calc",
            title = "Precision Calc",
            subtitle = "Scientific & Unit Engine",
            category = AppCategory.PRODUCTIVITY,
            isBuiltIn = true,
            accentHex = 0xFF5E5CE6,
            iconKey = "calculator"
        ),
        AppItem(
            id = "builtin_diagnostics",
            title = "Diagnostics",
            subtitle = "Real Hardware & Memory",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF30D158,
            iconKey = "diagnostics"
        ),
        AppItem(
            id = "builtin_settings",
            title = "Settings",
            subtitle = "Appearance, Permissions & Reset",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF64748B,
            iconKey = "settings"
        ),
        AppItem(
            id = "builtin_camera",
            title = "Optics Cam",
            subtitle = "Launch Android Camera",
            category = AppCategory.MEDIA_WELLNESS,
            isBuiltIn = true,
            accentHex = 0xFF3B82F6,
            iconKey = "camera"
        ),
        AppItem(
            id = "builtin_control",
            title = "Control Hub",
            subtitle = "Quick System Toggles",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF0EA5E9,
            iconKey = "control"
        ),
        AppItem(
            id = "builtin_notifications",
            title = "Inbox Center",
            subtitle = "Live & Demo Alerts",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFFEC4899,
            iconKey = "notifications",
            badgeCount = 4
        ),
        AppItem(
            id = "builtin_lock",
            title = "Lock Screen",
            subtitle = "Enter Lock Simulation",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF8B5CF6,
            iconKey = "lock"
        ),
        AppItem(
            id = "builtin_dialer",
            title = "Phone Link",
            subtitle = "System Dialer Shortcut",
            category = AppCategory.ESSENTIALS,
            isBuiltIn = true,
            accentHex = 0xFF22C55E,
            iconKey = "phone"
        )
    )

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())

    val orderedApps: StateFlow<List<AppItem>> = combine(
        _installedApps,
        userSettings
    ) { installed, settings ->
        val combined = builtInApps + installed
        if (settings.customAppOrderIds.isEmpty()) {
            combined
        } else {
            val byId = combined.associateBy { it.id }
            val ordered = settings.customAppOrderIds.mapNotNull { byId[it] }
            val remaining = combined.filter { it.id !in settings.customAppOrderIds }
            ordered + remaining
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = builtInApps
    )

    // Shell Navigation States
    private val _isLockScreenVisible = MutableStateFlow(false)
    val isLockScreenVisible: StateFlow<Boolean> = _isLockScreenVisible.asStateFlow()

    private val _isControlCenterOpen = MutableStateFlow(false)
    val isControlCenterOpen: StateFlow<Boolean> = _isControlCenterOpen.asStateFlow()

    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    private val _isAppLibraryOpen = MutableStateFlow(false)
    val isAppLibraryOpen: StateFlow<Boolean> = _isAppLibraryOpen.asStateFlow()

    private val _isDynamicCapsuleExpanded = MutableStateFlow(false)
    val isDynamicCapsuleExpanded: StateFlow<Boolean> = _isDynamicCapsuleExpanded.asStateFlow()

    private val _activeMicroApp = MutableStateFlow<BuiltInMicroApp?>(null)
    val activeMicroApp: StateFlow<BuiltInMicroApp?> = _activeMicroApp.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    // Interactive Audio & Timer states that integrate with Dynamic Capsule & Control Center
    private val _audioSession = MutableStateFlow(ActiveAudioSessionState())
    val audioSession: StateFlow<ActiveAudioSessionState> = _audioSession.asStateFlow()

    private val _timerState = MutableStateFlow(ActiveTimerState())
    val timerState: StateFlow<ActiveTimerState> = _timerState.asStateFlow()

    private val _quickNoteText = MutableStateFlow(
        "• Review Aether OS 18 tactile gesture response\n• Test real Android brightness & torch callbacks\n• Customize Lock Screen clock typography"
    )
    val quickNoteText: StateFlow<String> = _quickNoteText.asStateFlow()

    private var timerJob: Job? = null
    private var bannerClearJob: Job? = null

    private val initialDemoNotifications: List<NotificationItem> = listOf(
        NotificationItem(
            id = "demo_1",
            appId = "builtin_weather",
            appName = "Aether Weather",
            title = "Clear Evening Sky Ahead",
            message = "19°C • Crisp visibility with light coastal breeze tonight.",
            detailedBody = "Barometric pressure is steady at 1018 hPa. UV index is 0 (Low). Overnight low will reach 13°C with clear stargazing conditions.",
            timestampMillis = System.currentTimeMillis() - 4 * 60_000L,
            isRealSystemNotification = false,
            categoryTag = "Weather",
            accentHex = 0xFF32ADE6
        ),
        NotificationItem(
            id = "demo_2",
            appId = "builtin_diagnostics",
            appName = "System Architecture",
            title = "60/120Hz Frame Pacing Active",
            message = "Zero real-time blur overhead • Lifecycle-scoped listeners verified.",
            detailedBody = "Aether OS 18 is running in native Jetpack Compose mode with hardware-accelerated vector surfaces, zero background polling, and DataStore persistence.",
            timestampMillis = System.currentTimeMillis() - 18 * 60_000L,
            isRealSystemNotification = false,
            categoryTag = "Performance",
            accentHex = 0xFF30D158
        ),
        NotificationItem(
            id = "demo_3",
            appId = "builtin_notes",
            appName = "Studio Notes",
            title = "Design System Checklist Pinned",
            message = "2 items synced to local scratchpad.",
            detailedBody = "Tap to inspect or edit your pinned Studio Notes directly inside the built-in workspace.",
            timestampMillis = System.currentTimeMillis() - 42 * 60_000L,
            isRealSystemNotification = false,
            categoryTag = "Productivity",
            accentHex = 0xFFF59E0B
        ),
        NotificationItem(
            id = "demo_4",
            appId = "builtin_audio",
            appName = "Pulse Audio",
            title = "Spatial Mix Ready",
            message = "Titanium Horizon • Lossless acoustic preset loaded.",
            detailedBody = "Start playback from Control Center or Dynamic Capsule to experience live media session telemetry.",
            timestampMillis = System.currentTimeMillis() - 95 * 60_000L,
            isRealSystemNotification = false,
            categoryTag = "Media",
            accentHex = 0xFFFF375F
        )
    )

    private val _demoNotifications = MutableStateFlow(initialDemoNotifications)
    val demoNotifications: StateFlow<List<NotificationItem>> = _demoNotifications.asStateFlow()

    private val _expandedNotificationIds = MutableStateFlow<Set<String>>(emptySet())
    val expandedNotificationIds: StateFlow<Set<String>> = _expandedNotificationIds.asStateFlow()

    init {
        refreshInstalledApps()
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            val apps = systemManager.queryInstalledLauncherApps()
            _installedApps.value = apps
        }
    }

    fun showLockScreen(visible: Boolean) {
        _isLockScreenVisible.value = visible
        if (visible) {
            _isControlCenterOpen.value = false
            _isNotificationCenterOpen.value = false
            _isAppLibraryOpen.value = false
        }
    }

    fun setControlCenterOpen(open: Boolean) {
        _isControlCenterOpen.value = open
        if (open) {
            _isNotificationCenterOpen.value = false
            _isDynamicCapsuleExpanded.value = false
            systemManager.refreshSystemSnapshot()
        }
    }

    fun setNotificationCenterOpen(open: Boolean) {
        _isNotificationCenterOpen.value = open
        if (open) {
            _isControlCenterOpen.value = false
            _isDynamicCapsuleExpanded.value = false
            systemManager.refreshSystemSnapshot()
        }
    }

    fun setAppLibraryOpen(open: Boolean) {
        _isAppLibraryOpen.value = open
        if (open) {
            _isControlCenterOpen.value = false
            _isNotificationCenterOpen.value = false
        } else {
            _searchQuery.value = ""
        }
    }

    fun toggleDynamicCapsuleExpanded() {
        _isDynamicCapsuleExpanded.update { !it }
    }

    fun setDynamicCapsuleExpanded(expanded: Boolean) {
        _isDynamicCapsuleExpanded.value = expanded
    }

    fun openMicroApp(app: BuiltInMicroApp?) {
        _activeMicroApp.value = app
        if (app != null) {
            _isControlCenterOpen.value = false
            _isNotificationCenterOpen.value = false
            _isLockScreenVisible.value = false
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateQuickNote(text: String) {
        _quickNoteText.value = text
    }

    fun launchAppItem(item: AppItem, view: View) {
        systemManager.performHaptic(view, userSettings.value.hapticsEnabled)
        when (item.id) {
            "builtin_weather" -> openMicroApp(BuiltInMicroApp.WEATHER)
            "builtin_timer" -> openMicroApp(BuiltInMicroApp.CHRONO_TIMER)
            "builtin_audio" -> openMicroApp(BuiltInMicroApp.PULSE_AUDIO)
            "builtin_notes" -> openMicroApp(BuiltInMicroApp.QUICK_NOTES)
            "builtin_calc" -> openMicroApp(BuiltInMicroApp.CALCULATOR)
            "builtin_diagnostics" -> openMicroApp(BuiltInMicroApp.DIAGNOSTICS)
            "builtin_settings" -> openMicroApp(BuiltInMicroApp.SETTINGS)
            "builtin_camera" -> {
                val ok = systemManager.launchSystemCamera()
                showBanner(if (ok) "Launching Android Camera…" else "Opened System Settings")
            }
            "builtin_control" -> setControlCenterOpen(true)
            "builtin_notifications" -> setNotificationCenterOpen(true)
            "builtin_lock" -> showLockScreen(true)
            "builtin_dialer" -> {
                val opened = systemManager.openSystemSettings( android.content.Intent.ACTION_DIAL )
                if (!opened) showBanner("Dialer unavailable on this device")
            }
            else -> {
                val pkg = item.packageName
                if (!pkg.isNullOrBlank()) {
                    val launched = systemManager.launchInstalledApp(pkg, item.activityName)
                    if (!launched) {
                        showBanner("Could not launch ${item.title}")
                    }
                }
            }
        }
    }

    fun triggerLockShortcut(action: LockShortcutAction, view: View) {
        systemManager.performHaptic(view, userSettings.value.hapticsEnabled, isHeavy = true)
        when (action) {
            LockShortcutAction.FLASHLIGHT -> {
                val msg = systemManager.toggleFlashlight()
                showBanner(msg)
            }
            LockShortcutAction.CAMERA_APP -> {
                systemManager.launchSystemCamera()
            }
            LockShortcutAction.CONTROL_CENTER -> {
                setControlCenterOpen(true)
            }
            LockShortcutAction.QUICK_TIMER -> {
                showLockScreen(false)
                startOrToggleTimer(180, "Quick 3m Timer")
                openMicroApp(BuiltInMicroApp.CHRONO_TIMER)
            }
            LockShortcutAction.QUICK_NOTES -> {
                showLockScreen(false)
                openMicroApp(BuiltInMicroApp.QUICK_NOTES)
            }
            LockShortcutAction.DND_TOGGLE -> {
                val toggled = systemManager.toggleOrRequestDnd()
                showBanner(
                    if (toggled) "Do Not Disturb updated"
                    else "Grant DND Policy Access in Android Settings"
                )
            }
        }
    }

    // Audio playback controls
    private val audioTracks = listOf(
        "Titanium Horizon (Spatial Mix)" to "Aether Acoustic Lab",
        "Subtle Glass Resonance" to "Minimalist Ensemble",
        "Deep Sapphire Nocturne" to "Analog Synthesis Studio"
    )
    private var trackIndex = 0

    fun toggleAudioPlayback() {
        _audioSession.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun nextAudioTrack() {
        trackIndex = (trackIndex + 1) % audioTracks.size
        val (title, artist) = audioTracks[trackIndex]
        _audioSession.update {
            it.copy(
                trackTitle = title,
                artistName = artist,
                progressFraction = 0.12f,
                isPlaying = true
            )
        }
    }

    fun previousAudioTrack() {
        trackIndex = if (trackIndex - 1 < 0) audioTracks.lastIndex else trackIndex - 1
        val (title, artist) = audioTracks[trackIndex]
        _audioSession.update {
            it.copy(
                trackTitle = title,
                artistName = artist,
                progressFraction = 0.05f,
                isPlaying = true
            )
        }
    }

    fun seekAudio(fraction: Float) {
        _audioSession.update { it.copy(progressFraction = fraction.coerceIn(0f, 1f)) }
    }

    // Timer controls (zero background polling when timer is stopped)
    fun startOrToggleTimer(durationSec: Int = _timerState.value.totalSeconds, label: String = _timerState.value.label) {
        val current = _timerState.value
        if (current.isRunning) {
            timerJob?.cancel()
            _timerState.update { it.copy(isRunning = false) }
        } else {
            val initialRem = if (current.remainingSeconds <= 0 || durationSec != current.totalSeconds) {
                durationSec
            } else {
                current.remainingSeconds
            }
            _timerState.value = ActiveTimerState(
                isRunning = true,
                totalSeconds = durationSec,
                remainingSeconds = initialRem,
                label = label
            )
            startTimerLoop()
        }
    }

    fun resetTimer(durationSec: Int = _timerState.value.totalSeconds, label: String = _timerState.value.label) {
        timerJob?.cancel()
        _timerState.value = ActiveTimerState(
            isRunning = false,
            totalSeconds = durationSec,
            remainingSeconds = durationSec,
            label = label
        )
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.isRunning && _timerState.value.remainingSeconds > 0) {
                delay(1_000L)
                val next = _timerState.value.remainingSeconds - 1
                if (next <= 0) {
                    _timerState.update { it.copy(isRunning = false, remainingSeconds = 0) }
                    showBanner("Chrono Timer complete: ${_timerState.value.label}")
                } else {
                    _timerState.update { it.copy(remainingSeconds = next) }
                }
            }
        }
    }

    // Notification Center interactions
    fun toggleNotificationExpanded(id: String) {
        _expandedNotificationIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun dismissDemoNotification(id: String) {
        _demoNotifications.update { list -> list.filterNot { it.id == id } }
    }

    fun clearAllDemoNotifications() {
        _demoNotifications.value = emptyList()
        showBanner("Cleared local demonstration notifications")
    }

    fun restoreDemoNotifications() {
        _demoNotifications.value = initialDemoNotifications
        showBanner("Restored demonstration notifications")
    }

    // Hardware & System Control Center Actions
    fun onFlashlightToggle() {
        val status = systemManager.toggleFlashlight()
        showBanner(status)
    }

    fun onDndToggle() {
        val changed = systemManager.toggleOrRequestDnd()
        if (changed) {
            val active = telemetry.value.dndActive
            showBanner(if (active) "Do Not Disturb Enabled" else "Do Not Disturb Disabled")
        } else {
            showBanner("Opening Android DND Policy Access Settings…")
        }
    }

    fun onAutoRotateToggle() {
        val changed = systemManager.toggleOrRequestAutoRotate()
        if (changed) {
            val active = telemetry.value.autoRotateEnabled
            showBanner(if (active) "Auto-Rotate Unlocked" else "Portrait Orientation Locked")
        } else {
            showBanner("Opening Android Display Settings…")
        }
    }

    fun onBrightnessChange(activity: Activity?, percent: Int) {
        systemManager.setBrightnessPercent(activity, percent)
    }

    fun onMediaVolumeChange(volume: Int) {
        systemManager.setMediaVolume(volume)
    }

    fun onRingVolumeChange(volume: Int) {
        systemManager.setRingVolume(volume)
    }

    // Persisted Settings Mutations
    fun setThemePreference(preference: ThemePreference) {
        viewModelScope.launch { settingsRepository.setThemePreference(preference) }
    }

    fun setWallpaperPreset(preset: WallpaperPreset) {
        viewModelScope.launch {
            settingsRepository.setWallpaperPreset(preset)
            showBanner("Wallpaper set to ${preset.title}")
        }
    }

    fun setClockStyle(style: ClockStyle) {
        viewModelScope.launch { settingsRepository.setClockStyle(style) }
    }

    fun setLockShortcut(isLeft: Boolean, action: LockShortcutAction) {
        viewModelScope.launch { settingsRepository.setLockShortcut(isLeft, action) }
    }

    fun setGridDensity(density: GridDensity) {
        viewModelScope.launch { settingsRepository.setGridDensity(density) }
    }

    fun setIconSize(size: IconSizeOption) {
        viewModelScope.launch { settingsRepository.setIconSize(size) }
    }

    fun setIconTheme(theme: IconThemeOption) {
        viewModelScope.launch { settingsRepository.setIconTheme(theme) }
    }

    fun setAnimationIntensity(intensity: AnimationIntensity) {
        viewModelScope.launch { settingsRepository.setAnimationIntensity(intensity) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHapticsEnabled(enabled) }
    }

    fun setGroupNotificationsByApp(grouped: Boolean) {
        viewModelScope.launch { settingsRepository.setGroupNotificationsByApp(grouped) }
    }

    fun toggleFavoriteApp(appId: String) {
        viewModelScope.launch { settingsRepository.toggleFavoriteApp(appId) }
    }

    fun toggleDockApp(appId: String) {
        viewModelScope.launch {
            settingsRepository.toggleDockApp(appId)
            showBanner("Updated bottom dock")
        }
    }

    fun moveAppInGrid(appId: String, moveEarlier: Boolean) {
        viewModelScope.launch {
            val currentIds = orderedApps.value.map { it.id }.toMutableList()
            val idx = currentIds.indexOf(appId)
            if (idx == -1) return@launch
            val targetIdx = if (moveEarlier) (idx - 1).coerceAtLeast(0) else (idx + 1).coerceAtMost(currentIds.lastIndex)
            if (idx != targetIdx) {
                val item = currentIds.removeAt(idx)
                currentIds.add(targetIdx, item)
                settingsRepository.saveCustomAppOrder(currentIds)
                showBanner("Updated Home Grid icon position")
            }
        }
    }

    fun resetAllSettingsToDefault() {
        viewModelScope.launch {
            settingsRepository.resetToDefaults()
            _demoNotifications.value = initialDemoNotifications
            showBanner("All settings & layouts restored to default")
        }
    }

    fun showBanner(message: String) {
        bannerClearJob?.cancel()
        _statusBannerMessage.value = message
        bannerClearJob = viewModelScope.launch {
            delay(2_600L)
            _statusBannerMessage.value = null
        }
    }

    /**
     * Handles Android system back navigation predictably through open overlays before exiting.
     */
    fun hasOpenOverlay(): Boolean {
        return _isDynamicCapsuleExpanded.value ||
            _isControlCenterOpen.value ||
            _isNotificationCenterOpen.value ||
            _activeMicroApp.value != null ||
            _isAppLibraryOpen.value ||
            _isLockScreenVisible.value
    }

    fun handleBackNavigation(): Boolean {
        return when {
            _isDynamicCapsuleExpanded.value -> {
                _isDynamicCapsuleExpanded.value = false
                true
            }
            _isControlCenterOpen.value -> {
                _isControlCenterOpen.value = false
                true
            }
            _isNotificationCenterOpen.value -> {
                _isNotificationCenterOpen.value = false
                true
            }
            _activeMicroApp.value != null -> {
                _activeMicroApp.value = null
                true
            }
            _isAppLibraryOpen.value -> {
                _isAppLibraryOpen.value = false
                _searchQuery.value = ""
                true
            }
            _isLockScreenVisible.value -> {
                _isLockScreenVisible.value = false
                true
            }
            else -> false
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val appCtx = context.applicationContext
            val repo = SettingsRepository(appCtx.aetherDataStore)
            val sysMgr = SystemIntegrationManager(appCtx)
            return AetherViewModel(repo, sysMgr) as T
        }
    }
}
