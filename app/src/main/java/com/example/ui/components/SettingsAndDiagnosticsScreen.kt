package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AnimationIntensity
import com.example.model.ClockStyle
import com.example.model.GridDensity
import com.example.model.IconSizeOption
import com.example.model.IconThemeOption
import com.example.model.LockShortcutAction
import com.example.model.RealSystemTelemetry
import com.example.model.ThemePreference
import com.example.model.UserSettings
import com.example.model.WallpaperPreset
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CyanAether
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.SapphireAccentLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreenContent(
    settings: UserSettings,
    telemetry: RealSystemTelemetry,
    onSelectTheme: (ThemePreference) -> Unit,
    onSelectWallpaper: (WallpaperPreset) -> Unit,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectLockShortcut: (Boolean, LockShortcutAction) -> Unit,
    onSelectGridDensity: (GridDensity) -> Unit,
    onSelectIconSize: (IconSizeOption) -> Unit,
    onSelectIconTheme: (IconThemeOption) -> Unit,
    onSelectAnimationIntensity: (AnimationIntensity) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onOpenWriteSettingsPermission: () -> Unit,
    onOpenDndPermission: () -> Unit,
    onOpenNotificationListenerPermission: () -> Unit,
    onOpenDefaultLauncherSettings: () -> Unit,
    onResetAllToDefaults: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Appearance & Theme Mode
        SettingsCardSection(
            title = "APPEARANCE & SYSTEM THEME",
            icon = Icons.Filled.Palette
        ) {
            Text(
                text = "Persisted automatically in Jetpack DataStore across application restarts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ThemePreference.entries.forEach { pref ->
                    FilterChip(
                        selected = settings.themePreference == pref,
                        onClick = { onSelectTheme(pref) },
                        label = { Text(pref.label) },
                        modifier = Modifier.testTag("theme_chip_${pref.name}")
                    )
                }
            }
        }

        // 2. Wallpaper Selection (Contrast-Aware)
        SettingsCardSection(
            title = "WALLPAPER & AUTO-CONTRAST ATMOSPHERE",
            icon = Icons.Filled.Palette
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WallpaperPreset.entries.forEach { preset ->
                    val selected = settings.wallpaperPreset == preset
                    Surface(
                        onClick = { onSelectWallpaper(preset) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wallpaper_option_${preset.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Brush.verticalGradient(preset.primaryColors))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${preset.subtitle} • ${if (preset.hasLightUpperArea) "Dark Ink Text" else "Light Frost Text"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Home Grid Density, Icon Size & Icon Theme
        SettingsCardSection(
            title = "HOME SCREEN GRID & ICON GEOMETRY",
            icon = Icons.Filled.Home
        ) {
            Text(
                text = "Grid Density",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GridDensity.entries.forEach { density ->
                    FilterChip(
                        selected = settings.gridDensity == density,
                        onClick = { onSelectGridDensity(density) },
                        label = { Text(density.label) },
                        modifier = Modifier.testTag("grid_density_${density.id}")
                    )
                }
            }

            Text(
                text = "Icon Size",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconSizeOption.entries.forEach { sizeOpt ->
                    FilterChip(
                        selected = settings.iconSize == sizeOpt,
                        onClick = { onSelectIconSize(sizeOpt) },
                        label = { Text(sizeOpt.label) },
                        modifier = Modifier.testTag("icon_size_${sizeOpt.id}")
                    )
                }
            }

            Text(
                text = "Icon Surface Theme",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconThemeOption.entries.forEach { themeOpt ->
                    FilterChip(
                        selected = settings.iconTheme == themeOpt,
                        onClick = { onSelectIconTheme(themeOpt) },
                        label = { Text(themeOpt.label) },
                        modifier = Modifier.testTag("icon_theme_${themeOpt.id}")
                    )
                }
            }
        }

        // 4. Lock Screen Clock & Shortcuts
        SettingsCardSection(
            title = "LOCK SCREEN TYPOGRAPHY & SHORTCUTS",
            icon = Icons.Filled.Security
        ) {
            Text(
                text = "Clock Typography",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClockStyle.entries.forEach { style ->
                    FilterChip(
                        selected = settings.clockStyle == style,
                        onClick = { onSelectClockStyle(style) },
                        label = { Text(style.label) }
                    )
                }
            }

            Text(
                text = "Bottom-Left Shortcut",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LockShortcutAction.entries.forEach { action ->
                    FilterChip(
                        selected = settings.leftLockShortcut == action,
                        onClick = { onSelectLockShortcut(true, action) },
                        label = { Text(action.label) }
                    )
                }
            }

            Text(
                text = "Bottom-Right Shortcut",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LockShortcutAction.entries.forEach { action ->
                    FilterChip(
                        selected = settings.rightLockShortcut == action,
                        onClick = { onSelectLockShortcut(false, action) },
                        label = { Text(action.label) }
                    )
                }
            }
        }

        // 5. Animation Intensity & Haptics
        SettingsCardSection(
            title = "MOTION PHYSICS & HAPTIC FEEDBACK",
            icon = Icons.Filled.Speed
        ) {
            Text(
                text = "Animation Intensity (Interface remains 100% usable when Off)",
                style = MaterialTheme.typography.labelLarge
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnimationIntensity.entries.forEach { intensity ->
                    FilterChip(
                        selected = settings.animationIntensity == intensity,
                        onClick = { onSelectAnimationIntensity(intensity) },
                        label = { Text(intensity.label) },
                        modifier = Modifier.testTag("anim_intensity_${intensity.id}")
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tactile Haptic Feedback",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Subtle Android View haptics on shortcuts and app launches",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.hapticsEnabled,
                    onCheckedChange = onToggleHaptics,
                    modifier = Modifier.testTag("haptics_toggle_switch")
                )
            }
        }

        // 6. Privacy, Permissions & Special Access Center
        SettingsCardSection(
            title = "PRIVACY, PERMISSIONS & ANDROID ACCESS",
            icon = Icons.Filled.Security
        ) {
            PermissionRowItem(
                title = "Modify System Settings (OS Brightness & Auto-Rotate)",
                subtitle = if (telemetry.canWriteSystemSettings) {
                    "Authorized • Writes directly to Settings.System"
                } else {
                    "Not granted • Window brightness works immediately; tap to authorize OS-wide write"
                },
                isAuthorized = telemetry.canWriteSystemSettings,
                onClick = onOpenWriteSettingsPermission
            )

            PermissionRowItem(
                title = "Do Not Disturb Policy Access",
                subtitle = if (telemetry.canAccessDndPolicy) {
                    "Authorized • Controls real NotificationManager interruption filter"
                } else {
                    "Not granted • Tap to open Android DND Policy Access Settings"
                },
                isAuthorized = telemetry.canAccessDndPolicy,
                onClick = onOpenDndPermission
            )

            PermissionRowItem(
                title = "Notification Listener Bridge (Optional)",
                subtitle = if (telemetry.notificationListenerGranted) {
                    "Authorized • Mirrors live Android notifications in Notification Center"
                } else {
                    "Not granted • Only local demo notifications shown until authorized"
                },
                isAuthorized = telemetry.notificationListenerGranted,
                onClick = onOpenNotificationListenerPermission
            )

            PermissionRowItem(
                title = "Android Default Home App Settings",
                subtitle = "Aether OS 18 runs as a safe in-app experience. Tap to view Android's Default Home Settings.",
                isAuthorized = true,
                onClick = onOpenDefaultLauncherSettings
            )
        }

        // 7. Reset to Defaults
        Button(
            onClick = onResetAllToDefaults,
            colors = ButtonDefaults.buttonColors(
                containerColor = CoralAlert,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("reset_all_settings_button")
        ) {
            Icon(imageVector = Icons.Filled.RestartAlt, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.reset_to_defaults),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DiagnosticsScreenContent(
    telemetry: RealSystemTelemetry,
    settings: UserSettings,
    installedAppsCount: Int
) {
    val runtimeStats = remember(telemetry) {
        val rt = Runtime.getRuntime()
        val usedMb = ((rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)).coerceAtLeast(1)
        val maxMb = (rt.maxMemory() / (1024 * 1024)).coerceAtLeast(1)
        usedMb to maxMb
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsCardSection(
            title = "REAL-TIME PERFORMANCE & MEMORY TELEMETRY",
            icon = Icons.Filled.Speed
        ) {
            DiagnosticMetricRow("JVM Heap Allocation", "${runtimeStats.first} MB / ${runtimeStats.second} MB Max")
            DiagnosticMetricRow("Android SDK / API Level", "API ${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})")
            DiagnosticMetricRow("Device Hardware", "${Build.MANUFACTURER} ${Build.MODEL}")
            DiagnosticMetricRow("Motion Engine", "${settings.animationIntensity.label} (${settings.animationIntensity.description})")
            DiagnosticMetricRow("Real-Time Blur Shaders", "0 (Zero GPU Overdraw Architecture)")
            DiagnosticMetricRow("Background Polling Loops", "0 (Lifecycle-Scoped Event Receivers)")
            DiagnosticMetricRow("Discovered Installed Apps", "$installedAppsCount Launchable Packages")
            DiagnosticMetricRow("Hardware Flash Unit", if (telemetry.flashlightAvailable) "Present (CameraManager)" else "Absent (Screen Torch Fallback)")
        }

        SettingsCardSection(
            title = "ARCHITECTURAL HONESTY MATRIX (REAL VS IN-APP SIMULATION)",
            icon = Icons.Filled.Info
        ) {
            Text(
                text = "• Native Android Integrations: AudioManager (Music & Ring streams), CameraManager (Torch mode), BatteryManager sticky telemetry, ConnectivityManager & WifiManager status, PackageManager app discovery & launching, Settings.System brightness & auto-rotate, NotificationManager DND filter, and optional NotificationListenerService.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• In-App Simulations (Platform Restricted): Lock Screen overlay (preserves Android's real biometric/keyguard security), Dynamic Capsule status pill (in-app header without deceptive system overlays), and Local Demonstration Notifications.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsCardSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
            }
            content()
        }
    }
}

@Composable
private fun PermissionRowItem(
    title: String,
    subtitle: String,
    isAuthorized: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(if (isAuthorized) EmeraldActive.copy(alpha = 0.2f) else AmberWarm.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isAuthorized) "Active" else "Configure",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isAuthorized) EmeraldActive else AmberWarm,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun DiagnosticMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
