package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.example.R

enum class ThemePreference(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Alabaster Light"),
    DARK("Obsidian Dark")
}

enum class WallpaperPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val hasLightUpperArea: Boolean,
    @DrawableRes val drawableResId: Int?,
    val primaryColors: List<Color>,
    val accentGlowColor: Color
) {
    TITANIUM_SILK(
        id = "titanium_silk",
        title = "Titanium Silk",
        subtitle = "Deep navy & warm amber silk ribbons",
        hasLightUpperArea = false,
        drawableResId = R.drawable.img_wallpaper_titanium_silk_1793244159345,
        primaryColors = listOf(Color(0xFF050811), Color(0xFF0E172A), Color(0xFF1E293B)),
        accentGlowColor = Color(0xFFFF9F0A)
    ),
    PEARL_DUNE(
        id = "pearl_dune",
        title = "Pearl Dune",
        subtitle = "Alabaster silk & sky-blue refraction",
        hasLightUpperArea = true,
        drawableResId = R.drawable.img_wallpaper_pearl_dune_1793244180680,
        primaryColors = listOf(Color(0xFFF5F3EF), Color(0xFFEAE3D5), Color(0xFFD6E4F0)),
        accentGlowColor = Color(0xFF32ADE6)
    ),
    OBSIDIAN_HORIZON(
        id = "obsidian_horizon",
        title = "Obsidian Horizon",
        subtitle = "Minimalist midnight sapphire & cobalt",
        hasLightUpperArea = false,
        drawableResId = null,
        primaryColors = listOf(Color(0xFF060913), Color(0xFF0F1B33), Color(0xFF193058)),
        accentGlowColor = Color(0xFF2E7CF6)
    ),
    SOLAR_ALABASTER(
        id = "solar_alabaster",
        title = "Solar Alabaster",
        subtitle = "Warm daylight pearl & soft champagne",
        hasLightUpperArea = true,
        drawableResId = null,
        primaryColors = listOf(Color(0xFFFAF8F5), Color(0xFFF0EAE1), Color(0xFFE2E8F4)),
        accentGlowColor = Color(0xFFF59E0B)
    ),
    DEEP_SAPPHIRE(
        id = "deep_sapphire",
        title = "Deep Sapphire",
        subtitle = "Architectural indigo & cyan zenith",
        hasLightUpperArea = false,
        drawableResId = null,
        primaryColors = listOf(Color(0xFF070B19), Color(0xFF131C3E), Color(0xFF1D3566)),
        accentGlowColor = Color(0xFF38BDF8)
    ),
    EMERALD_MIST(
        id = "emerald_mist",
        title = "Emerald Carbon",
        subtitle = "Subtle botanical slate & mineral teal",
        hasLightUpperArea = false,
        drawableResId = null,
        primaryColors = listOf(Color(0xFF06100E), Color(0xFF0E2420), Color(0xFF163832)),
        accentGlowColor = Color(0xFF30D158)
    );

    companion object {
        fun fromId(id: String?): WallpaperPreset =
            entries.firstOrNull { it.id == id } ?: TITANIUM_SILK
    }
}

enum class ClockStyle(val id: String, val label: String) {
    CLASSIC_ROUNDED("classic", "Classic Pro"),
    GEOMETRIC_BOLD("geometric", "Geometric Heavy"),
    MINIMAL_STENCIL("minimal", "Ultra Thin"),
    EDITORIAL_COMPACT("editorial", "Editorial Split");

    companion object {
        fun fromId(id: String?): ClockStyle =
            entries.firstOrNull { it.id == id } ?: CLASSIC_ROUNDED
    }
}

enum class LockShortcutAction(val id: String, val label: String) {
    FLASHLIGHT("flashlight", "Flashlight"),
    CAMERA_APP("camera", "Camera"),
    CONTROL_CENTER("control_center", "Control Panel"),
    QUICK_TIMER("timer", "Chrono Timer"),
    QUICK_NOTES("notes", "Quick Note"),
    DND_TOGGLE("dnd", "Focus / DND");

    companion object {
        fun fromId(id: String?, default: LockShortcutAction): LockShortcutAction =
            entries.firstOrNull { it.id == id } ?: default
    }
}

enum class GridDensity(val id: String, val label: String, val columns: Int, val rowsPerPage: Int) {
    COMFORTABLE_4X4("4x4", "Standard (4×4)", 4, 4),
    COMPACT_4X5("4x5", "Dense (4×5)", 4, 5),
    SPACIOUS_3X4("3x4", "Spacious (3×4)", 3, 4);

    companion object {
        fun fromId(id: String?): GridDensity =
            entries.firstOrNull { it.id == id } ?: COMFORTABLE_4X4
    }
}

enum class IconSizeOption(val id: String, val label: String, val sizeDp: Int) {
    SMALL("small", "Compact (54dp)", 54),
    MEDIUM("medium", "Balanced (62dp)", 62),
    LARGE("large", "Expressive (70dp)", 70);

    companion object {
        fun fromId(id: String?): IconSizeOption =
            entries.firstOrNull { it.id == id } ?: MEDIUM
    }
}

enum class IconThemeOption(val id: String, val label: String) {
    DEFAULT_TINTED("default", "Signature Tinted"),
    MONOCHROME_GLASS("mono", "Monochrome Frosted"),
    TITANIUM_DARK("titanium", "Obsidian Titanium"),
    VIBRANT_FROST("vibrant", "Luminous Studio");

    companion object {
        fun fromId(id: String?): IconThemeOption =
            entries.firstOrNull { it.id == id } ?: DEFAULT_TINTED
    }
}

enum class AnimationIntensity(val id: String, val label: String, val description: String) {
    FLUID("fluid", "Fluid (Spring)", "Full 60/120Hz spring physics"),
    STANDARD("standard", "Balanced", "Crisp, critically-damped transitions"),
    REDUCED("reduced", "Reduced Motion", "Fast subtle crossfades"),
    OFF("off", "Instant (Off)", "Zero animation delay for maximum speed");

    companion object {
        fun fromId(id: String?): AnimationIntensity =
            entries.firstOrNull { it.id == id } ?: FLUID
    }
}

@Immutable
data class UserSettings(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val wallpaperPreset: WallpaperPreset = WallpaperPreset.TITANIUM_SILK,
    val clockStyle: ClockStyle = ClockStyle.CLASSIC_ROUNDED,
    val leftLockShortcut: LockShortcutAction = LockShortcutAction.FLASHLIGHT,
    val rightLockShortcut: LockShortcutAction = LockShortcutAction.CAMERA_APP,
    val gridDensity: GridDensity = GridDensity.COMFORTABLE_4X4,
    val iconSize: IconSizeOption = IconSizeOption.MEDIUM,
    val iconTheme: IconThemeOption = IconThemeOption.DEFAULT_TINTED,
    val animationIntensity: AnimationIntensity = AnimationIntensity.FLUID,
    val hapticsEnabled: Boolean = true,
    val groupNotificationsByApp: Boolean = false,
    val favoriteAppIds: Set<String> = setOf("builtin_weather", "builtin_timer", "builtin_audio", "builtin_settings"),
    val customAppOrderIds: List<String> = emptyList(),
    val dockAppIds: List<String> = listOf("builtin_dialer", "builtin_audio", "builtin_notes", "builtin_settings")
)

enum class AppCategory(val label: String) {
    ESSENTIALS("System & Essentials"),
    PRODUCTIVITY("Productivity & Studio"),
    MEDIA_WELLNESS("Audio & Atmosphere"),
    INSTALLED_ANDROID("Installed Android Apps")
}

@Immutable
data class AppItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: AppCategory,
    val packageName: String? = null,
    val activityName: String? = null,
    val isBuiltIn: Boolean = true,
    val accentHex: Long = 0xFF2E7CF6,
    val iconKey: String = "sparkles",
    val badgeCount: Int = 0
)

@Immutable
data class NotificationItem(
    val id: String,
    val appId: String,
    val appName: String,
    val title: String,
    val message: String,
    val detailedBody: String,
    val timestampMillis: Long,
    val isRealSystemNotification: Boolean = false,
    val categoryTag: String = "Update",
    val accentHex: Long = 0xFF2E7CF6
)

@Immutable
data class RealSystemTelemetry(
    val batteryPercent: Int = 88,
    val isCharging: Boolean = false,
    val batteryTempCelsius: Float = 27.5f,
    val wifiEnabled: Boolean = true,
    val networkConnected: Boolean = true,
    val networkTransportLabel: String = "Wi-Fi",
    val bluetoothEnabled: Boolean = false,
    val bluetoothSupported: Boolean = true,
    val mediaVolume: Int = 8,
    val mediaMaxVolume: Int = 15,
    val ringVolume: Int = 5,
    val ringMaxVolume: Int = 7,
    val brightnessPercent: Int = 72,
    val canWriteSystemSettings: Boolean = false,
    val flashlightAvailable: Boolean = false,
    val flashlightActive: Boolean = false,
    val screenTorchFallbackActive: Boolean = false,
    val dndActive: Boolean = false,
    val canAccessDndPolicy: Boolean = false,
    val autoRotateEnabled: Boolean = true,
    val notificationListenerGranted: Boolean = false,
    val systemMediaPlaying: Boolean = false
)

enum class BuiltInMicroApp(val appId: String, val title: String) {
    WEATHER("builtin_weather", "Aether Weather"),
    CHRONO_TIMER("builtin_timer", "Chrono Timer"),
    PULSE_AUDIO("builtin_audio", "Pulse Audio Studio"),
    QUICK_NOTES("builtin_notes", "Quick Notes"),
    DIAGNOSTICS("builtin_diagnostics", "System Diagnostics"),
    CALCULATOR("builtin_calc", "Precision Calc"),
    SETTINGS("builtin_settings", "System Settings")
}

@Immutable
data class ActiveAudioSessionState(
    val isPlaying: Boolean = false,
    val trackTitle: String = "Titanium Horizon (Spatial Mix)",
    val artistName: String = "Aether Acoustic Lab",
    val progressFraction: Float = 0.36f,
    val durationSeconds: Int = 214
)

@Immutable
data class ActiveTimerState(
    val isRunning: Boolean = false,
    val totalSeconds: Int = 180,
    val remainingSeconds: Int = 180,
    val label: String = "Focus Sprint"
)
