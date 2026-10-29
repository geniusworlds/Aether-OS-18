package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ActiveAudioSessionState
import com.example.model.BuiltInMicroApp
import com.example.model.RealSystemTelemetry
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CyanAether
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.IndigoFocus
import com.example.ui.theme.SapphireAccentLight

@Composable
fun ControlCenterOverlay(
    telemetry: RealSystemTelemetry,
    audioSession: ActiveAudioSessionState,
    onDismiss: () -> Unit,
    onOpenWifiSettings: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onToggleDnd: () -> Unit,
    onToggleAutoRotate: () -> Unit,
    onBrightnessChange: (Activity?, Int) -> Unit,
    onOpenWriteSettingsPermission: () -> Unit,
    onMediaVolumeChange: (Int) -> Unit,
    onRingVolumeChange: (Int) -> Unit,
    onToggleAudioPlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onOpenMicroApp: (BuiltInMicroApp) -> Unit,
    onLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var connectivityExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE0070B14))
            .clickable(onClick = onDismiss)
            .testTag("control_center_overlay"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .clickable(enabled = false, onClick = {})
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.control_center_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Real Android Hardware & System API Bridge",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8)
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B2436))
                        .testTag("close_control_center_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Control Center",
                        tint = Color.White
                    )
                }
            }

            // Top Bento Grid: Connectivity Card + Media Playback Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Connectivity Card (Expandable)
                Surface(
                    onClick = { connectivityExpanded = !connectivityExpanded },
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xD9151D2E),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
                        .testTag("cc_connectivity_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CONNECTIVITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Icon(
                                imageVector = if (connectivityExpanded) {
                                    Icons.Filled.ExpandLess
                                } else {
                                    Icons.Filled.ExpandMore
                                },
                                contentDescription = "Expand Connectivity",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Wi-Fi Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onOpenWifiSettings)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (telemetry.wifiEnabled) SapphireAccentLight else Color(0xFF283246)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Wifi,
                                    contentDescription = "Wi-Fi",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Wi-Fi",
                                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                                )
                                Text(
                                    text = telemetry.networkTransportLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Bluetooth Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onOpenBluetoothSettings)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (telemetry.bluetoothEnabled) SapphireAccentLight else Color(0xFF283246)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bluetooth,
                                    contentDescription = "Bluetooth",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Bluetooth",
                                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                                )
                                Text(
                                    text = if (!telemetry.bluetoothSupported) {
                                        "Not Supported"
                                    } else if (telemetry.bluetoothEnabled) {
                                        "On • Settings"
                                    } else {
                                        "Off • Tap to Manage"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Media Session Card
                Surface(
                    onClick = { onOpenMicroApp(BuiltInMicroApp.PULSE_AUDIO) },
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xD9151D2E),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
                        .testTag("cc_media_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NOW PLAYING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFFF375F),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = Color(0xFFFF375F),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = audioSession.trackTitle,
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = audioSession.artistName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onPrevTrack, modifier = Modifier.size(34.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = onToggleAudioPlay,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SapphireAccentLight)
                                    .testTag("cc_media_play_pause")
                            ) {
                                Icon(
                                    imageVector = if (audioSession.isPlaying) {
                                        Icons.Filled.Pause
                                    } else {
                                        Icons.Filled.PlayArrow
                                    },
                                    contentDescription = "Play/Pause",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = onNextTrack, modifier = Modifier.size(34.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = "Next",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Expanded Connectivity Details Drawer
            AnimatedVisibility(visible = connectivityExpanded) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF111827),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ANDROID CONNECTIVITY SHORTCUTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAether,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Android 10+ restricts apps from silently toggling Wi-Fi or Bluetooth without user interaction. Tap below to open Android's native system panels.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFCBD5E1)
                            )
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                onClick = onOpenWifiSettings,
                                shape = RoundedCornerShape(14.dp),
                                color = SapphireAccentLight
                            ) {
                                Text(
                                    text = "Open Wi-Fi Panel",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                            Surface(
                                onClick = onOpenBluetoothSettings,
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF263147)
                            ) {
                                Text(
                                    text = "Bluetooth Settings",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Hardware & System Action Tiles Row 1 (Placed prominently above sliders for instant reachability)
            val isTorchOn = telemetry.flashlightActive || telemetry.screenTorchFallbackActive
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ControlActionTile(
                    title = "Flashlight",
                    subtitle = when {
                        telemetry.flashlightActive -> "Hardware On"
                        telemetry.screenTorchFallbackActive -> "Screen Torch"
                        telemetry.flashlightAvailable -> "Hardware Ready"
                        else -> "Screen Fallback"
                    },
                    icon = Icons.Filled.FlashlightOn,
                    isActive = isTorchOn,
                    activeColor = AmberWarm,
                    onClick = onToggleFlashlight,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_flashlight")
                )

                ControlActionTile(
                    title = "Focus / DND",
                    subtitle = when {
                        !telemetry.canAccessDndPolicy -> "Grant Access"
                        telemetry.dndActive -> "DND Active"
                        else -> "All Alerts On"
                    },
                    icon = Icons.Filled.DoNotDisturbOn,
                    isActive = telemetry.dndActive,
                    activeColor = IndigoFocus,
                    onClick = onToggleDnd,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_dnd")
                )
            }

            // Hardware & System Action Tiles Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ControlActionTile(
                    title = "Orientation",
                    subtitle = if (telemetry.autoRotateEnabled) "Auto-Rotate" else "Portrait Locked",
                    icon = Icons.Filled.ScreenRotation,
                    isActive = !telemetry.autoRotateEnabled,
                    activeColor = SapphireAccentLight,
                    onClick = onToggleAutoRotate,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_rotation")
                )

                ControlActionTile(
                    title = "Chrono Timer",
                    subtitle = "Capsule Countdown",
                    icon = Icons.Filled.AccessTime,
                    isActive = false,
                    activeColor = AmberWarm,
                    onClick = { onOpenMicroApp(BuiltInMicroApp.CHRONO_TIMER) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cc_tile_timer")
                )
            }

            // Brightness & Audio Sliders Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xD9151D2E),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Display Brightness Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BrightnessHigh,
                                contentDescription = null,
                                tint = AmberWarm,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Display Brightness",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                            )
                        }
                        Text(
                            text = "${telemetry.brightnessPercent}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberWarm,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Slider(
                        value = telemetry.brightnessPercent.toFloat(),
                        onValueChange = { onBrightnessChange(activity, it.toInt()) },
                        valueRange = 5f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberWarm,
                            activeTrackColor = AmberWarm,
                            inactiveTrackColor = Color.White.copy(alpha = 0.16f)
                        ),
                        modifier = Modifier.testTag("cc_brightness_slider")
                    )

                    if (!telemetry.canWriteSystemSettings) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1F293D))
                                .clickable(onClick = onOpenWriteSettingsPermission)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Window brightness active • Tap to authorize OS-wide write",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Grant",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SapphireAccentLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Media Volume Slider (Real Android AudioManager STREAM_MUSIC)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = SapphireAccentLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Media Volume (Android Audio)",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                            )
                        }
                        Text(
                            text = "${telemetry.mediaVolume} / ${telemetry.mediaMaxVolume}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SapphireAccentLight,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Slider(
                        value = telemetry.mediaVolume.toFloat(),
                        onValueChange = { onMediaVolumeChange(it.toInt()) },
                        valueRange = 0f..telemetry.mediaMaxVolume.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = SapphireAccentLight,
                            activeTrackColor = SapphireAccentLight,
                            inactiveTrackColor = Color.White.copy(alpha = 0.16f)
                        ),
                        modifier = Modifier.testTag("cc_media_volume_slider")
                    )

                    // Ring / Notification Volume Slider (Real Android AudioManager STREAM_RING)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = CyanAether,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Ringer & Alerts Volume",
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                            )
                        }
                        Text(
                            text = "${telemetry.ringVolume} / ${telemetry.ringMaxVolume}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAether,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Slider(
                        value = telemetry.ringVolume.toFloat(),
                        onValueChange = { onRingVolumeChange(it.toInt()) },
                        valueRange = 0f..telemetry.ringMaxVolume.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAether,
                            activeTrackColor = CyanAether,
                            inactiveTrackColor = Color.White.copy(alpha = 0.16f)
                        )
                    )
                }
            }

            // Bottom Row: Battery Telemetry, System Settings, and Lock Simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ControlActionTile(
                    title = "Battery ${telemetry.batteryPercent}%",
                    subtitle = if (telemetry.isCharging) {
                        "Charging • ${telemetry.batteryTempCelsius}°C"
                    } else {
                        "Discharging • ${telemetry.batteryTempCelsius}°C"
                    },
                    icon = if (telemetry.isCharging) {
                        Icons.Filled.BatteryChargingFull
                    } else {
                        Icons.Filled.BatteryFull
                    },
                    isActive = telemetry.isCharging,
                    activeColor = EmeraldActive,
                    onClick = { onOpenMicroApp(BuiltInMicroApp.DIAGNOSTICS) },
                    modifier = Modifier.weight(1f)
                )

                ControlActionTile(
                    title = "Lock Screen",
                    subtitle = "Enter Lock Sim",
                    icon = Icons.Filled.Lock,
                    isActive = false,
                    activeColor = SapphireAccentLight,
                    onClick = onLockScreen,
                    modifier = Modifier.weight(1f)
                )

                ControlActionTile(
                    title = "Settings",
                    subtitle = "Customize OS",
                    icon = Icons.Filled.Settings,
                    isActive = false,
                    activeColor = CyanAether,
                    onClick = { onOpenMicroApp(BuiltInMicroApp.SETTINGS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ControlActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (isActive) activeColor else Color(0xD9151D2E),
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (isActive) 0.35f else 0.12f),
                shape = RoundedCornerShape(22.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) Color.White else activeColor,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isActive) Color.White.copy(alpha = 0.88f) else Color(0xFF94A3B8),
                        fontSize = 10.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
