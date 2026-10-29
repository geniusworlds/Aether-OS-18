package com.example.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.AnimationIntensity
import com.example.model.ThemePreference

private val AetherDarkColorScheme = darkColorScheme(
    primary = SapphireAccentLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF16325C),
    onPrimaryContainer = Color(0xFFD6E6FF),
    secondary = CyanAether,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF15384B),
    onSecondaryContainer = Color(0xFFC7EEFF),
    tertiary = IndigoFocus,
    background = ObsidianBackground,
    onBackground = ObsidianTextPrimary,
    surface = ObsidianSurface,
    onSurface = ObsidianTextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = ObsidianTextSecondary,
    outline = ObsidianBorder,
    error = CoralAlert
)

private val AetherLightColorScheme = lightColorScheme(
    primary = SapphireAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE9FF),
    onPrimaryContainer = Color(0xFF0A2547),
    secondary = CyanAether,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9F2FF),
    onSecondaryContainer = Color(0xFF062E42),
    tertiary = IndigoFocus,
    background = AlabasterBackground,
    onBackground = AlabasterTextPrimary,
    surface = AlabasterSurface,
    onSurface = AlabasterTextPrimary,
    surfaceVariant = AlabasterSurfaceElevated,
    onSurfaceVariant = AlabasterTextSecondary,
    outline = AlabasterBorder,
    error = CoralAlert
)

val AetherShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Immutable
data class AetherSurfaceTokens(
    val isDark: Boolean,
    val translucentSurface: Color,
    val translucentElevated: Color,
    val subtleBorder: Color,
    val pillBackground: Color,
    val dockBackground: Color,
    val activeTileBackground: Color,
    val inactiveTileBackground: Color
)

val LocalAetherSurfaceTokens = staticCompositionLocalOf {
    AetherSurfaceTokens(
        isDark = true,
        translucentSurface = ObsidianTranslucentCard,
        translucentElevated = Color(0xE6222B3E),
        subtleBorder = ObsidianBorder,
        pillBackground = Color(0xFF05070B),
        dockBackground = Color(0xBF161D2B),
        activeTileBackground = SapphireAccent,
        inactiveTileBackground = Color(0xB3232C3F)
    )
}

val LocalAnimationIntensity = staticCompositionLocalOf { AnimationIntensity.FLUID }

object AetherMotion {
    fun <T> springSpec(intensity: AnimationIntensity): FiniteAnimationSpec<T> {
        return when (intensity) {
            AnimationIntensity.OFF -> tween(durationMillis = 0)
            AnimationIntensity.REDUCED -> tween(durationMillis = 140)
            AnimationIntensity.STANDARD -> spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
            AnimationIntensity.FLUID -> spring(
                dampingRatio = 0.82f,
                stiffness = 380f
            )
        }
    }

    fun durationMillis(intensity: AnimationIntensity, baseMs: Int = 260): Int {
        return when (intensity) {
            AnimationIntensity.OFF -> 0
            AnimationIntensity.REDUCED -> (baseMs * 0.45f).toInt()
            AnimationIntensity.STANDARD -> baseMs
            AnimationIntensity.FLUID -> (baseMs * 1.1f).toInt()
        }
    }
}

@Composable
fun MyApplicationTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    animationIntensity: AnimationIntensity = AnimationIntensity.FLUID,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themePreference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
    }

    val colorScheme = if (useDark) AetherDarkColorScheme else AetherLightColorScheme
    val surfaceTokens = if (useDark) {
        AetherSurfaceTokens(
            isDark = true,
            translucentSurface = Color(0xD9151C2A),
            translucentElevated = Color(0xEB1F283B),
            subtleBorder = Color(0x29FFFFFF),
            pillBackground = Color(0xFF04060A),
            dockBackground = Color(0xCC151C2B),
            activeTileBackground = SapphireAccentLight,
            inactiveTileBackground = Color(0xB8212A3D)
        )
    } else {
        AetherSurfaceTokens(
            isDark = false,
            translucentSurface = Color(0xE6FFFFFF),
            translucentElevated = Color(0xF2F3F6FC),
            subtleBorder = Color(0x1F0F172A),
            pillBackground = Color(0xFF0A0E17),
            dockBackground = Color(0xD9FFFFFF),
            activeTileBackground = SapphireAccent,
            inactiveTileBackground = Color(0xCCEDF1F8)
        )
    }

    CompositionLocalProvider(
        LocalAetherSurfaceTokens provides surfaceTokens,
        LocalAnimationIntensity provides animationIntensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = AetherShapes,
            content = content
        )
    }
}
