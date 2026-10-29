package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ClockStyle
import com.example.model.LockShortcutAction
import com.example.model.NotificationItem
import com.example.model.RealSystemTelemetry
import com.example.model.UserSettings
import com.example.model.WallpaperPreset
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.SapphireAccentLight
import com.example.ui.theme.SpaceGroteskFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

@Composable
fun LockScreenOverlay(
    settings: UserSettings,
    telemetry: RealSystemTelemetry,
    notifications: List<NotificationItem>,
    onUnlock: () -> Unit,
    onTriggerShortcut: (LockShortcutAction, android.view.View) -> Unit,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectWallpaper: (WallpaperPreset) -> Unit,
    onOpenNotificationCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var isCustomizing by remember { mutableStateOf(false) }

    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(10_000L)
        }
    }

    val timeText = remember(nowMillis) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(nowMillis))
    }
    val dateText = remember(nowMillis) {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date(nowMillis))
    }

    val hasLightTop = settings.wallpaperPreset.hasLightUpperArea
    val primaryTextColor = if (hasLightTop) Color(0xFF0B111E) else Color(0xFFF8FAFC)
    val secondaryTextColor = if (hasLightTop) Color(0xFF334155) else Color(0xFFCBD5E1)
    val pillSurfaceColor = if (hasLightTop) Color(0xCCFFFFFF) else Color(0xB3121826)

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffsetY < -140f) {
                            dragOffsetY = 0f
                            onUnlock()
                        } else {
                            dragOffsetY = 0f
                        }
                    },
                    onDragCancel = { dragOffsetY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        val next = (dragOffsetY + dragAmount).coerceAtMost(0f)
                        dragOffsetY = next
                    }
                )
            }
            .testTag("lock_screen_overlay")
    ) {
        WallpaperBackground(
            preset = settings.wallpaperPreset,
            dimAmount = if (isCustomizing) 0.35f else 0.08f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, (dragOffsetY * 0.35f).roundToInt()) }
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Platform Honesty & Security Badge + Customize Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(pillSurfaceColor)
                        .border(1.dp, primaryTextColor.copy(alpha = 0.14f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = SapphireAccentLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.lock_screen_sim_badge),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = primaryTextColor
                    )
                }

                Surface(
                    onClick = { isCustomizing = !isCustomizing },
                    shape = RoundedCornerShape(50.dp),
                    color = pillSurfaceColor,
                    modifier = Modifier.testTag("lock_customize_toggle_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Customize Lock Screen",
                            tint = primaryTextColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isCustomizing) "Done" else "Customize",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = primaryTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Date & Day Header
            Text(
                text = dateText.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = secondaryTextColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Large Elegant Digital Clock (4 Selectable Typography Styles)
            LockScreenClockDisplay(
                timeText = timeText,
                clockStyle = settings.clockStyle,
                textColor = primaryTextColor
            )

            // Weather & Battery Status Pill below Clock
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(pillSurfaceColor)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "19°C Clear Sky",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = primaryTextColor
                )
                Text(
                    text = "•",
                    color = secondaryTextColor
                )
                Text(
                    text = "Battery ${telemetry.batteryPercent}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = primaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Inline Lock Screen Customizer Drawer (Clock Style & Wallpaper Switcher)
            AnimatedVisibility(visible = isCustomizing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 520.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xD90E1422))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(24.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CLOCK TYPOGRAPHY",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(ClockStyle.entries) { style ->
                            FilterChip(
                                selected = settings.clockStyle == style,
                                onClick = { onSelectClockStyle(style) },
                                label = { Text(style.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SapphireAccentLight,
                                    selectedLabelColor = Color.White,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Text(
                        text = "WALLPAPER ATMOSPHERE (AUTO CONTRAST)",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(WallpaperPreset.entries) { wp ->
                            FilterChip(
                                selected = settings.wallpaperPreset == wp,
                                onClick = { onSelectWallpaper(wp) },
                                label = { Text(wp.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SapphireAccentLight,
                                    selectedLabelColor = Color.White,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Minimal Notification Previews Stack
            if (notifications.isNotEmpty() && !isCustomizing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT NOTIFICATIONS (DEMO PREVIEW)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = secondaryTextColor
                        )
                        Text(
                            text = "View All (${notifications.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SapphireAccentLight
                            ),
                            modifier = Modifier.clickable {
                                onUnlock()
                                onOpenNotificationCenter()
                            }
                        )
                    }

                    notifications.take(2).forEach { item ->
                        Surface(
                            onClick = {
                                onUnlock()
                                onOpenNotificationCenter()
                            },
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xCC121826),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(22.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(item.accentHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.appName.take(1),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "now",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF94A3B8)
                                            )
                                        )
                                    }
                                    Text(
                                        text = item.message,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFFCBD5E1)
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Row: Left Shortcut, Unlock Pill, Right Shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leftActive = settings.leftLockShortcut == LockShortcutAction.FLASHLIGHT &&
                    (telemetry.flashlightActive || telemetry.screenTorchFallbackActive)

                LockScreenShortcutCircle(
                    action = settings.leftLockShortcut,
                    isActive = leftActive,
                    onClick = { onTriggerShortcut(settings.leftLockShortcut, view) },
                    testTag = "lock_left_shortcut_button"
                )

                // Center Unlock Button & Gesture Indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onUnlock)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("unlock_home_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = "Unlock to Home",
                            tint = primaryTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.swipe_up_to_unlock),
                            style = MaterialTheme.typography.labelLarge,
                            color = primaryTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .width(134.dp)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(primaryTextColor.copy(alpha = 0.85f))
                    )
                }

                val rightActive = settings.rightLockShortcut == LockShortcutAction.FLASHLIGHT &&
                    (telemetry.flashlightActive || telemetry.screenTorchFallbackActive)

                LockScreenShortcutCircle(
                    action = settings.rightLockShortcut,
                    isActive = rightActive,
                    onClick = { onTriggerShortcut(settings.rightLockShortcut, view) },
                    testTag = "lock_right_shortcut_button"
                )
            }
        }
    }
}

@Composable
private fun LockScreenClockDisplay(
    timeText: String,
    clockStyle: ClockStyle,
    textColor: Color
) {
    when (clockStyle) {
        ClockStyle.CLASSIC_ROUNDED -> {
            Text(
                text = timeText,
                fontFamily = PlusJakartaSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 84.sp,
                letterSpacing = (-2.5).sp,
                color = textColor
            )
        }
        ClockStyle.GEOMETRIC_BOLD -> {
            Text(
                text = timeText,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 88.sp,
                letterSpacing = (-3.5).sp,
                color = textColor
            )
        }
        ClockStyle.MINIMAL_STENCIL -> {
            Text(
                text = timeText,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 82.sp,
                letterSpacing = 2.sp,
                color = textColor
            )
        }
        ClockStyle.EDITORIAL_COMPACT -> {
            val parts = timeText.split(":")
            val hrs = parts.getOrNull(0) ?: "12"
            val mins = parts.getOrNull(1) ?: "00"
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = hrs,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 84.sp,
                    color = textColor
                )
                Text(
                    text = ":$mins",
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 52.sp,
                    color = textColor.copy(alpha = 0.82f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun LockScreenShortcutCircle(
    action: LockShortcutAction,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val icon: ImageVector = when (action) {
        LockShortcutAction.FLASHLIGHT -> Icons.Filled.FlashlightOn
        LockShortcutAction.CAMERA_APP -> Icons.Filled.CameraAlt
        LockShortcutAction.CONTROL_CENTER -> Icons.Filled.Tune
        LockShortcutAction.QUICK_TIMER -> Icons.Filled.AccessTime
        LockShortcutAction.QUICK_NOTES -> Icons.Filled.EditNote
        LockShortcutAction.DND_TOGGLE -> Icons.Filled.DoNotDisturbOn
    }

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isActive) AmberWarm else Color(0xB3161E2E),
        modifier = Modifier
            .size(54.dp)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.24f),
                shape = CircleShape
            )
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = action.label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
