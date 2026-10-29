package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.IconThemeOption
import com.example.model.WallpaperPreset

@Composable
fun WallpaperBackground(
    preset: WallpaperPreset,
    dimAmount: Float = 0f,
    modifier: Modifier = Modifier
) {
    val linearBrush = remember(preset) {
        Brush.verticalGradient(colors = preset.primaryColors)
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (preset.drawableResId != null) {
            Image(
                painter = painterResource(id = preset.drawableResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(brush = linearBrush)
                // Subtle architectural radial light glow (single pass, zero allocation)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            preset.accentGlowColor.copy(alpha = 0.24f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.78f, size.height * 0.28f),
                        radius = size.minDimension * 0.75f
                    ),
                    radius = size.minDimension * 0.75f,
                    center = Offset(size.width * 0.78f, size.height * 0.28f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            preset.primaryColors.last().copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.78f),
                        radius = size.minDimension * 0.85f
                    ),
                    radius = size.minDimension * 0.85f,
                    center = Offset(size.width * 0.2f, size.height * 0.78f)
                )
            }
        }

        if (dimAmount > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dimAmount.coerceIn(0f, 0.85f)))
            )
        }
    }
}

fun resolveAppIconVector(iconKey: String): ImageVector {
    return when (iconKey) {
        "weather" -> Icons.Filled.Cloud
        "timer" -> Icons.Filled.AccessTime
        "audio" -> Icons.Filled.GraphicEq
        "notes" -> Icons.Filled.EditNote
        "calculator" -> Icons.Filled.Calculate
        "diagnostics" -> Icons.Filled.Speed
        "settings" -> Icons.Filled.Settings
        "camera" -> Icons.Filled.CameraAlt
        "control" -> Icons.Filled.Tune
        "notifications" -> Icons.Filled.Notifications
        "lock" -> Icons.Filled.Lock
        "phone" -> Icons.Filled.Phone
        else -> Icons.Filled.Android
    }
}

@Composable
fun AetherAppIconGraphic(
    app: AppItem,
    iconSize: Dp,
    iconTheme: IconThemeOption,
    modifier: Modifier = Modifier
) {
    val cornerRadius = iconSize * 0.26f
    val shape = RoundedCornerShape(cornerRadius)
    val baseAccent = Color(app.accentHex)

    val containerBrush = remember(app.accentHex, iconTheme) {
        when (iconTheme) {
            IconThemeOption.DEFAULT_TINTED -> Brush.linearGradient(
                colors = listOf(
                    baseAccent.copy(alpha = 0.95f),
                    baseAccent.copy(
                        red = (baseAccent.red * 0.68f).coerceIn(0f, 1f),
                        green = (baseAccent.green * 0.68f).coerceIn(0f, 1f),
                        blue = (baseAccent.blue * 0.78f).coerceIn(0f, 1f),
                        alpha = 1f
                    )
                )
            )
            IconThemeOption.MONOCHROME_GLASS -> Brush.linearGradient(
                colors = listOf(
                    Color(0xD9283142),
                    Color(0xD9161C28)
                )
            )
            IconThemeOption.TITANIUM_DARK -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A202E),
                    Color(0xFF0D1119)
                )
            )
            IconThemeOption.VIBRANT_FROST -> Brush.linearGradient(
                colors = listOf(
                    baseAccent,
                    Color(0xFF1E293B)
                )
            )
        }
    }

    val iconTint = remember(app.accentHex, iconTheme) {
        when (iconTheme) {
            IconThemeOption.DEFAULT_TINTED -> Color.White
            IconThemeOption.MONOCHROME_GLASS -> Color(0xFFE2E8F0)
            IconThemeOption.TITANIUM_DARK -> baseAccent.copy(
                red = (baseAccent.red + 0.22f).coerceAtMost(1f),
                green = (baseAccent.green + 0.22f).coerceAtMost(1f),
                blue = (baseAccent.blue + 0.22f).coerceAtMost(1f)
            )
            IconThemeOption.VIBRANT_FROST -> Color.White
        }
    }

    Box(modifier = modifier.size(iconSize)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(brush = containerBrush)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.24f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = resolveAppIconVector(app.iconKey),
                contentDescription = app.title,
                tint = iconTint,
                modifier = Modifier.size(iconSize * 0.48f)
            )
        }

        if (app.badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
