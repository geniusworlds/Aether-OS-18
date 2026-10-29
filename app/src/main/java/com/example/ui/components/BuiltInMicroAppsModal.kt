package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveAudioSessionState
import com.example.model.ActiveTimerState
import com.example.model.AnimationIntensity
import com.example.model.BuiltInMicroApp
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
import com.example.ui.theme.CyanAether
import com.example.ui.theme.SapphireAccentLight

@Composable
fun BuiltInMicroAppModal(
    microApp: BuiltInMicroApp,
    settings: UserSettings,
    telemetry: RealSystemTelemetry,
    audioSession: ActiveAudioSessionState,
    timerState: ActiveTimerState,
    quickNoteText: String,
    installedAppsCount: Int,
    onClose: () -> Unit,
    onUpdateNote: (String) -> Unit,
    onStartOrToggleTimer: (Int, String) -> Unit,
    onResetTimer: (Int, String) -> Unit,
    onToggleAudioPlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onSeekAudio: (Float) -> Unit,
    onMediaVolumeChange: (Int) -> Unit,
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
    onResetAllToDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxSize()
            .testTag("micro_app_modal_${microApp.appId}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Window Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = microApp.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Aether OS 18 Native Workspace",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("close_micro_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close ${microApp.title}"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (microApp) {
                    BuiltInMicroApp.SETTINGS -> {
                        SettingsScreenContent(
                            settings = settings,
                            telemetry = telemetry,
                            onSelectTheme = onSelectTheme,
                            onSelectWallpaper = onSelectWallpaper,
                            onSelectClockStyle = onSelectClockStyle,
                            onSelectLockShortcut = onSelectLockShortcut,
                            onSelectGridDensity = onSelectGridDensity,
                            onSelectIconSize = onSelectIconSize,
                            onSelectIconTheme = onSelectIconTheme,
                            onSelectAnimationIntensity = onSelectAnimationIntensity,
                            onToggleHaptics = onToggleHaptics,
                            onOpenWriteSettingsPermission = onOpenWriteSettingsPermission,
                            onOpenDndPermission = onOpenDndPermission,
                            onOpenNotificationListenerPermission = onOpenNotificationListenerPermission,
                            onOpenDefaultLauncherSettings = onOpenDefaultLauncherSettings,
                            onResetAllToDefaults = onResetAllToDefaults
                        )
                    }
                    BuiltInMicroApp.DIAGNOSTICS -> {
                        DiagnosticsScreenContent(
                            telemetry = telemetry,
                            settings = settings,
                            installedAppsCount = installedAppsCount
                        )
                    }
                    BuiltInMicroApp.CHRONO_TIMER -> {
                        ChronoTimerWorkspace(
                            timerState = timerState,
                            onStartOrToggle = onStartOrToggleTimer,
                            onReset = onResetTimer
                        )
                    }
                    BuiltInMicroApp.PULSE_AUDIO -> {
                        PulseAudioWorkspace(
                            audioSession = audioSession,
                            telemetry = telemetry,
                            onTogglePlay = onToggleAudioPlay,
                            onNext = onNextTrack,
                            onPrev = onPrevTrack,
                            onSeek = onSeekAudio,
                            onVolumeChange = onMediaVolumeChange
                        )
                    }
                    BuiltInMicroApp.WEATHER -> {
                        WeatherWorkspace()
                    }
                    BuiltInMicroApp.QUICK_NOTES -> {
                        QuickNotesWorkspace(
                            noteText = quickNoteText,
                            onUpdateNote = onUpdateNote
                        )
                    }
                    BuiltInMicroApp.CALCULATOR -> {
                        PrecisionCalculatorWorkspace()
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChronoTimerWorkspace(
    timerState: ActiveTimerState,
    onStartOrToggle: (Int, String) -> Unit,
    onReset: (Int, String) -> Unit
) {
    val presets = listOf(
        60 to "1m Express",
        180 to "3m Focus Sprint",
        300 to "5m Tea Steep",
        900 to "15m Deep Work",
        1500 to "25m Pomodoro"
    )

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = timerState.label.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = AmberWarm,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = formatSecondsMinSec(timerState.remainingSeconds),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                modifier = Modifier.testTag("timer_display_value")
            )
            Text(
                text = "Live countdown syncs directly with the top Dynamic Capsule.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onStartOrToggle(timerState.totalSeconds, timerState.label) },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarm, contentColor = Color.Black),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.testTag("timer_start_pause_button")
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (timerState.isRunning) "Pause Timer" else "Start Timer",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onReset(timerState.totalSeconds, timerState.label) },
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Reset")
                }
            }

            Text(
                text = "QUICK PRESETS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presets.forEach { (sec, label) ->
                    FilterChip(
                        selected = timerState.totalSeconds == sec,
                        onClick = { onStartOrToggle(sec, label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PulseAudioWorkspace(
    audioSession: ActiveAudioSessionState,
    telemetry: RealSystemTelemetry,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChange: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "SPATIAL ACOUSTIC ENGINE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFFF375F),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = audioSession.trackTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = audioSession.artistName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Slider(
                value = audioSession.progressFraction,
                onValueChange = onSeek,
                valueRange = 0f..1f
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrev, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
                }
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SapphireAccentLight)
                ) {
                    Icon(
                        imageVector = if (audioSession.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
                }
            }

            Text(
                text = "System Media Volume: ${telemetry.mediaVolume} / ${telemetry.mediaMaxVolume}",
                style = MaterialTheme.typography.labelLarge
            )
            Slider(
                value = telemetry.mediaVolume.toFloat(),
                onValueChange = { onVolumeChange(it.toInt()) },
                valueRange = 0f..telemetry.mediaMaxVolume.toFloat().coerceAtLeast(1f)
            )
        }
    }
}

@Composable
private fun WeatherWorkspace() {
    val forecast = listOf(
        Triple("Today", "Clear Sky • 19°C", "Low 13°C • High 22°C"),
        Triple("Tomorrow", "Light Coastal Breeze • 21°C", "Low 14°C • High 23°C"),
        Triple("Friday", "Sunny & Crisp • 23°C", "Low 15°C • High 25°C"),
        Triple("Saturday", "High Altitude Clouds • 20°C", "Low 13°C • High 21°C")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0E1B33),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("CUPERTINO STATION", style = MaterialTheme.typography.labelSmall.copy(color = CyanAether))
                Text("19°C", style = MaterialTheme.typography.displayLarge.copy(color = Color.White))
                Text("Barometer 1018 hPa • Humidity 48% • UV Index 1", color = Color(0xFFCBD5E1))
            }
        }

        forecast.forEach { (day, summary, range) ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(day, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(summary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = range,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickNotesWorkspace(
    noteText: String,
    onUpdateNote: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "${noteText.length} characters • Instant Local Scratchpad",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = noteText,
            onValueChange = onUpdateNote,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .testTag("quick_notes_editor"),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun PrecisionCalculatorWorkspace() {
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf<String?>(null) }
    var resetOnNextDigit by remember { mutableStateOf(false) }

    fun onDigit(d: String) {
        if (display == "0" || resetOnNextDigit) {
            display = d
            resetOnNextDigit = false
        } else {
            display += d
        }
    }

    fun onOperator(op: String) {
        firstOperand = display.toDoubleOrNull() ?: 0.0
        pendingOp = op
        resetOnNextDigit = true
    }

    fun onEquals() {
        val a = firstOperand ?: return
        val b = display.toDoubleOrNull() ?: return
        val res = when (pendingOp) {
            "+" -> a + b
            "−" -> a - b
            "×" -> a * b
            "÷" -> if (b != 0.0) a / b else 0.0
            else -> b
        }
        display = if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.4f", res).trimEnd('0').trimEnd('.')
        firstOperand = null
        pendingOp = null
        resetOnNextDigit = true
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = display,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            val rows = listOf(
                listOf("C", "÷", "×", "−"),
                listOf("7", "8", "9", "+"),
                listOf("4", "5", "6", "="),
                listOf("1", "2", "3", "0")
            )
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        Button(
                            onClick = {
                                when (key) {
                                    "C" -> {
                                        display = "0"
                                        firstOperand = null
                                        pendingOp = null
                                    }
                                    "+", "−", "×", "÷" -> onOperator(key)
                                    "=" -> onEquals()
                                    else -> onDigit(key)
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                        ) {
                            Text(key, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
