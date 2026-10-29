package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveAudioSessionState
import com.example.model.ActiveTimerState
import com.example.model.AnimationIntensity
import com.example.model.RealSystemTelemetry
import com.example.ui.theme.AetherMotion
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CyanAether
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.SapphireAccentLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun DynamicCapsuleStatusBar(
    telemetry: RealSystemTelemetry,
    audioSession: ActiveAudioSessionState,
    timerState: ActiveTimerState,
    isExpanded: Boolean,
    hasLightBackground: Boolean,
    animationIntensity: AnimationIntensity,
    onToggleExpand: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onToggleAudioPlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onToggleFlashlight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(15_000L)
        }
    }
    val timeFormatted = remember(currentTimeMillis) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(currentTimeMillis))
    }

    val statusTint = if (hasLightBackground) Color(0xFF0E1422) else Color(0xFFF8FAFC)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Status Area: Time & Notification Drawer Trigger
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenNotifications)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("status_bar_left_notifications"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = statusTint
                )
                if (telemetry.dndActive) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = Icons.Filled.DoNotDisturbOn,
                        contentDescription = "Do Not Disturb Active",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Center Interactive Dynamic Capsule
            val capsuleShape = RoundedCornerShape(if (isExpanded) 28.dp else 50.dp)
            Box(
                modifier = Modifier
                    .widthIn(min = 128.dp, max = 380.dp)
                    .clip(capsuleShape)
                    .background(Color(0xFF06080E))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = if (isExpanded) 0.22f else 0.14f),
                        shape = capsuleShape
                    )
                    .clickable(onClick = onToggleExpand)
                    .animateContentSize(animationSpec = AetherMotion.springSpec(animationIntensity))
                    .testTag("dynamic_capsule_pill")
            ) {
                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        fadeIn(tween(150)) togetherWith fadeOut(tween(120))
                    },
                    label = "DynamicCapsuleContent"
                ) { expanded ->
                    if (!expanded) {
                        CompactCapsuleContent(
                            telemetry = telemetry,
                            audioSession = audioSession,
                            timerState = timerState
                        )
                    } else {
                        ExpandedCapsuleContent(
                            telemetry = telemetry,
                            audioSession = audioSession,
                            timerState = timerState,
                            onToggleAudioPlay = onToggleAudioPlay,
                            onNextTrack = onNextTrack,
                            onPrevTrack = onPrevTrack,
                            onToggleTimer = onToggleTimer,
                            onResetTimer = onResetTimer,
                            onToggleFlashlight = onToggleFlashlight,
                            onOpenControlCenter = onOpenControlCenter,
                            onOpenNotifications = onOpenNotifications
                        )
                    }
                }
            }

            // Right Status Area: Connectivity & Battery (Tappable for Control Center)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onOpenControlCenter)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("status_bar_right_control_center"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Wifi,
                    contentDescription = "Wi-Fi Status",
                    tint = if (telemetry.networkConnected) statusTint else statusTint.copy(alpha = 0.4f),
                    modifier = Modifier.size(15.dp)
                )
                Icon(
                    imageVector = if (telemetry.isCharging) {
                        Icons.Filled.BatteryChargingFull
                    } else {
                        Icons.Filled.BatteryFull
                    },
                    contentDescription = "Battery ${telemetry.batteryPercent}%",
                    tint = if (telemetry.isCharging) EmeraldActive else statusTint,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${telemetry.batteryPercent}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusTint
                )
            }
        }
    }
}

@Composable
private fun CompactCapsuleContent(
    telemetry: RealSystemTelemetry,
    audioSession: ActiveAudioSessionState,
    timerState: ActiveTimerState
) {
    val isTorchOn = telemetry.flashlightActive || telemetry.screenTorchFallbackActive
    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when {
            timerState.isRunning -> {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Timer Running",
                    tint = AmberWarm,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = formatSecondsMinSec(timerState.remainingSeconds),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AmberWarm,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            audioSession.isPlaying || telemetry.systemMediaPlaying -> {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = "Audio Active",
                    tint = Color(0xFFFF375F),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = audioSession.trackTitle.substringBefore(" ("),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 110.dp)
                )
            }
            isTorchOn -> {
                Icon(
                    imageVector = Icons.Filled.FlashlightOn,
                    contentDescription = "Torch On",
                    tint = AmberWarm,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "Torch Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AmberWarm,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldActive)
                )
                Text(
                    text = "Aether Capsule",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFD6E2F5),
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = null,
                    tint = SapphireAccentLight,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun ExpandedCapsuleContent(
    telemetry: RealSystemTelemetry,
    audioSession: ActiveAudioSessionState,
    timerState: ActiveTimerState,
    onToggleAudioPlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val isTorchOn = telemetry.flashlightActive || telemetry.screenTorchFallbackActive

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Media Session & Quick Torch/Timer Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = if (audioSession.isPlaying) Color(0xFFFF375F) else CyanAether,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = audioSession.trackTitle,
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (audioSession.isPlaying) {
                            "${audioSession.artistName} • Playing"
                        } else {
                            "${audioSession.artistName} • Paused"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onPrevTrack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onToggleAudioPlay,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SapphireAccentLight)
                        .testTag("capsule_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (audioSession.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (audioSession.isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onNextTrack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next Track",
                        tint = Color.White
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { audioSession.progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = SapphireAccentLight,
            trackColor = Color.White.copy(alpha = 0.14f)
        )

        // Live Timer & Quick Actions Strip inside Dynamic Capsule
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timer Pill
            Surface(
                onClick = onToggleTimer,
                shape = RoundedCornerShape(14.dp),
                color = if (timerState.isRunning) AmberWarm.copy(alpha = 0.22f) else Color(0xFF151C2C),
                modifier = Modifier
                    .weight(1f)
                    .testTag("capsule_timer_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Timer",
                        tint = AmberWarm,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = formatSecondsMinSec(timerState.remainingSeconds),
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                        )
                        Text(
                            text = if (timerState.isRunning) "Tap to Pause" else "Start Timer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Torch Pill
            Surface(
                onClick = onToggleFlashlight,
                shape = RoundedCornerShape(14.dp),
                color = if (isTorchOn) AmberWarm else Color(0xFF151C2C),
                modifier = Modifier.testTag("capsule_torch_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashlightOn,
                        contentDescription = "Toggle Torch",
                        tint = if (isTorchOn) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isTorchOn) "On" else "Torch",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isTorchOn) Color.Black else Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Inbox Shortcut
            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151C2C))
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Open Notification Center",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Control Center Shortcut
            IconButton(
                onClick = onOpenControlCenter,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF151C2C))
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Open Control Center",
                    tint = CyanAether,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun formatSecondsMinSec(totalSec: Int): String {
    val m = (totalSec / 60).coerceAtLeast(0)
    val s = (totalSec % 60).coerceAtLeast(0)
    return String.format(Locale.US, "%02d:%02d", m, s)
}
