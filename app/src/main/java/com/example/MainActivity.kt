package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AetherViewModel
import com.example.ui.components.AppLibraryAndSearchOverlay
import com.example.ui.components.BuiltInMicroAppModal
import com.example.ui.components.ControlCenterOverlay
import com.example.ui.components.DynamicCapsuleStatusBar
import com.example.ui.components.HomeScreenContent
import com.example.ui.components.LockScreenOverlay
import com.example.ui.components.NotificationCenterOverlay
import com.example.ui.components.WallpaperBackground
import com.example.ui.theme.AetherMotion
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel: AetherViewModel = viewModel(
                factory = AetherViewModel.Factory(context)
            )
            AetherSystemApp(viewModel = viewModel)
        }
    }
}

@Composable
fun AetherSystemApp(viewModel: AetherViewModel) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val orderedApps by viewModel.orderedApps.collectAsStateWithLifecycle()
    val liveNotifications by viewModel.liveSystemNotifications.collectAsStateWithLifecycle()
    val demoNotifications by viewModel.demoNotifications.collectAsStateWithLifecycle()
    val expandedNotifIds by viewModel.expandedNotificationIds.collectAsStateWithLifecycle()

    val isLockScreenVisible by viewModel.isLockScreenVisible.collectAsStateWithLifecycle()
    val isControlCenterOpen by viewModel.isControlCenterOpen.collectAsStateWithLifecycle()
    val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsStateWithLifecycle()
    val isAppLibraryOpen by viewModel.isAppLibraryOpen.collectAsStateWithLifecycle()
    val isCapsuleExpanded by viewModel.isDynamicCapsuleExpanded.collectAsStateWithLifecycle()
    val activeMicroApp by viewModel.activeMicroApp.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val audioSession by viewModel.audioSession.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val quickNoteText by viewModel.quickNoteText.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.systemManager.startObserving()
                Lifecycle.Event.ON_RESUME -> viewModel.systemManager.refreshSystemSnapshot()
                Lifecycle.Event.ON_STOP -> viewModel.systemManager.stopObserving()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.systemManager.stopObserving()
        }
    }

    val anyOverlayOpen = isCapsuleExpanded ||
        isControlCenterOpen ||
        isNotificationCenterOpen ||
        activeMicroApp != null ||
        isAppLibraryOpen ||
        isLockScreenVisible

    BackHandler(enabled = anyOverlayOpen) {
        viewModel.handleBackNavigation()
    }

    val animMs = AetherMotion.durationMillis(settings.animationIntensity, 240)

    MyApplicationTheme(
        themePreference = settings.themePreference,
        animationIntensity = settings.animationIntensity
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("aether_root_shell")
        ) {
            // 1. Persistent Wallpaper Background Layer
            WallpaperBackground(
                preset = settings.wallpaperPreset,
                dimAmount = if (isCapsuleExpanded) 0.28f else 0f
            )

            // 2. Primary Home Shell Layer (Top Dynamic Capsule + Home Grid + Dock)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                DynamicCapsuleStatusBar(
                    telemetry = telemetry,
                    audioSession = audioSession,
                    timerState = timerState,
                    isExpanded = isCapsuleExpanded,
                    hasLightBackground = settings.wallpaperPreset.hasLightUpperArea,
                    animationIntensity = settings.animationIntensity,
                    onToggleExpand = { viewModel.toggleDynamicCapsuleExpanded() },
                    onOpenNotifications = { viewModel.setNotificationCenterOpen(true) },
                    onOpenControlCenter = { viewModel.setControlCenterOpen(true) },
                    onToggleAudioPlay = { viewModel.toggleAudioPlayback() },
                    onNextTrack = { viewModel.nextAudioTrack() },
                    onPrevTrack = { viewModel.previousAudioTrack() },
                    onToggleTimer = { viewModel.startOrToggleTimer() },
                    onResetTimer = { viewModel.resetTimer() },
                    onToggleFlashlight = { viewModel.onFlashlightToggle() }
                )

                HomeScreenContent(
                    apps = orderedApps,
                    settings = settings,
                    telemetry = telemetry,
                    timerState = timerState,
                    onLaunchApp = { app, view -> viewModel.launchAppItem(app, view) },
                    onOpenMicroApp = { viewModel.openMicroApp(it) },
                    onOpenAppLibrary = { viewModel.setAppLibraryOpen(true) },
                    onOpenControlCenter = { viewModel.setControlCenterOpen(true) },
                    onOpenNotificationCenter = { viewModel.setNotificationCenterOpen(true) },
                    onEnterLockScreen = { viewModel.showLockScreen(true) },
                    onMoveAppOrder = { id, earlier -> viewModel.moveAppInGrid(id, earlier) },
                    onToggleFavorite = { viewModel.toggleFavoriteApp(it) },
                    onToggleDockPin = { viewModel.toggleDockApp(it) },
                    onOpenAndroidAppInfo = { pkg -> viewModel.systemManager.openAppDetailsSettings(pkg) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Built-In Micro-App Workspace Modal (Settings, Diagnostics, Weather, Chrono, Audio, Notes, Calc)
            AnimatedVisibility(
                visible = activeMicroApp != null,
                enter = fadeIn(tween(animMs)) + slideInVertically(tween(animMs)) { it / 8 },
                exit = fadeOut(tween(animMs)) + slideOutVertically(tween(animMs)) { it / 8 }
            ) {
                activeMicroApp?.let { currentApp ->
                    BuiltInMicroAppModal(
                        microApp = currentApp,
                        settings = settings,
                        telemetry = telemetry,
                        audioSession = audioSession,
                        timerState = timerState,
                        quickNoteText = quickNoteText,
                        installedAppsCount = orderedApps.count { !it.isBuiltIn },
                        onClose = { viewModel.openMicroApp(null) },
                        onUpdateNote = { viewModel.updateQuickNote(it) },
                        onStartOrToggleTimer = { sec, lbl -> viewModel.startOrToggleTimer(sec, lbl) },
                        onResetTimer = { sec, lbl -> viewModel.resetTimer(sec, lbl) },
                        onToggleAudioPlay = { viewModel.toggleAudioPlayback() },
                        onNextTrack = { viewModel.nextAudioTrack() },
                        onPrevTrack = { viewModel.previousAudioTrack() },
                        onSeekAudio = { viewModel.seekAudio(it) },
                        onMediaVolumeChange = { viewModel.onMediaVolumeChange(it) },
                        onSelectTheme = { viewModel.setThemePreference(it) },
                        onSelectWallpaper = { viewModel.setWallpaperPreset(it) },
                        onSelectClockStyle = { viewModel.setClockStyle(it) },
                        onSelectLockShortcut = { isLeft, act -> viewModel.setLockShortcut(isLeft, act) },
                        onSelectGridDensity = { viewModel.setGridDensity(it) },
                        onSelectIconSize = { viewModel.setIconSize(it) },
                        onSelectIconTheme = { viewModel.setIconTheme(it) },
                        onSelectAnimationIntensity = { viewModel.setAnimationIntensity(it) },
                        onToggleHaptics = { viewModel.setHapticsEnabled(it) },
                        onOpenWriteSettingsPermission = {
                            viewModel.systemManager.openWriteSettingsPermission()
                        },
                        onOpenDndPermission = {
                            viewModel.systemManager.toggleOrRequestDnd()
                        },
                        onOpenNotificationListenerPermission = {
                            viewModel.systemManager.openNotificationListenerSettings()
                        },
                        onOpenDefaultLauncherSettings = {
                            viewModel.systemManager.openDefaultHomeLauncherPicker()
                        },
                        onResetAllToDefaults = { viewModel.resetAllSettingsToDefault() }
                    )
                }
            }

            // 4. App Library & Global Search Overlay
            AnimatedVisibility(
                visible = isAppLibraryOpen,
                enter = fadeIn(tween(animMs)),
                exit = fadeOut(tween(animMs))
            ) {
                AppLibraryAndSearchOverlay(
                    apps = orderedApps,
                    settings = settings,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onLaunchApp = { app, view -> viewModel.launchAppItem(app, view) },
                    onToggleFavorite = { viewModel.toggleFavoriteApp(it) },
                    onDismiss = { viewModel.setAppLibraryOpen(false) }
                )
            }

            // 5. Notification Center Overlay
            AnimatedVisibility(
                visible = isNotificationCenterOpen,
                enter = fadeIn(tween(animMs)) + slideInVertically(tween(animMs)) { -it / 6 },
                exit = fadeOut(tween(animMs)) + slideOutVertically(tween(animMs)) { -it / 6 }
            ) {
                NotificationCenterOverlay(
                    liveNotifications = liveNotifications,
                    demoNotifications = demoNotifications,
                    expandedIds = expandedNotifIds,
                    groupByApp = settings.groupNotificationsByApp,
                    notificationListenerAuthorized = telemetry.notificationListenerGranted,
                    onDismissOverlay = { viewModel.setNotificationCenterOpen(false) },
                    onToggleExpandItem = { viewModel.toggleNotificationExpanded(it) },
                    onDismissDemoItem = { viewModel.dismissDemoNotification(it) },
                    onClearAllDemo = { viewModel.clearAllDemoNotifications() },
                    onRestoreDemo = { viewModel.restoreDemoNotifications() },
                    onToggleGroupByApp = { viewModel.setGroupNotificationsByApp(it) },
                    onOpenNotificationAccessSettings = {
                        viewModel.systemManager.openNotificationListenerSettings()
                    }
                )
            }

            // 6. Control Center Overlay
            AnimatedVisibility(
                visible = isControlCenterOpen,
                enter = fadeIn(tween(animMs)) + slideInVertically(tween(animMs)) { -it / 6 },
                exit = fadeOut(tween(animMs)) + slideOutVertically(tween(animMs)) { -it / 6 }
            ) {
                ControlCenterOverlay(
                    telemetry = telemetry,
                    audioSession = audioSession,
                    onDismiss = { viewModel.setControlCenterOpen(false) },
                    onOpenWifiSettings = { viewModel.systemManager.openWifiPanelOrSettings() },
                    onOpenBluetoothSettings = { viewModel.systemManager.openBluetoothSettings() },
                    onToggleFlashlight = { viewModel.onFlashlightToggle() },
                    onToggleDnd = { viewModel.onDndToggle() },
                    onToggleAutoRotate = { viewModel.onAutoRotateToggle() },
                    onBrightnessChange = { act, pct -> viewModel.onBrightnessChange(act, pct) },
                    onOpenWriteSettingsPermission = {
                        viewModel.systemManager.openWriteSettingsPermission()
                    },
                    onMediaVolumeChange = { viewModel.onMediaVolumeChange(it) },
                    onRingVolumeChange = { viewModel.onRingVolumeChange(it) },
                    onToggleAudioPlay = { viewModel.toggleAudioPlayback() },
                    onNextTrack = { viewModel.nextAudioTrack() },
                    onPrevTrack = { viewModel.previousAudioTrack() },
                    onOpenMicroApp = { viewModel.openMicroApp(it) },
                    onLockScreen = { viewModel.showLockScreen(true) }
                )
            }

            // 7. In-App Lock Screen Simulation Overlay
            AnimatedVisibility(
                visible = isLockScreenVisible,
                enter = fadeIn(tween(animMs)),
                exit = fadeOut(tween(animMs)) + slideOutVertically(tween(animMs)) { -it / 4 }
            ) {
                LockScreenOverlay(
                    settings = settings,
                    telemetry = telemetry,
                    notifications = demoNotifications,
                    onUnlock = { viewModel.showLockScreen(false) },
                    onTriggerShortcut = { act, view -> viewModel.triggerLockShortcut(act, view) },
                    onSelectClockStyle = { viewModel.setClockStyle(it) },
                    onSelectWallpaper = { viewModel.setWallpaperPreset(it) },
                    onOpenNotificationCenter = { viewModel.setNotificationCenterOpen(true) }
                )
            }

            // 8. Screen Torch Fallback Banner (when device has no physical flash unit)
            if (telemetry.screenTorchFallbackActive) {
                Surface(
                    onClick = { viewModel.onFlashlightToggle() },
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 56.dp)
                        .border(2.dp, AmberWarm, RoundedCornerShape(50.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlashlightOff,
                            contentDescription = "Turn off Screen Torch",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Screen Torch Active • Tap to Turn Off",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // 9. Non-Intrusive Feedback Pill Banner
            AnimatedVisibility(
                visible = statusBanner != null,
                enter = fadeIn(tween(150)) + slideInVertically { it / 2 },
                exit = fadeOut(tween(150)) + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 96.dp)
            ) {
                statusBanner?.let { msg ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xEB0F172A))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}
