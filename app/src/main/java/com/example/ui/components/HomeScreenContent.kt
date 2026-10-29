package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ActiveTimerState
import com.example.model.AppItem
import com.example.model.BuiltInMicroApp
import com.example.model.RealSystemTelemetry
import com.example.model.UserSettings
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CyanAether
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.LocalAetherSurfaceTokens
import com.example.ui.theme.SapphireAccentLight
import kotlin.math.ceil
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    apps: List<AppItem>,
    settings: UserSettings,
    telemetry: RealSystemTelemetry,
    timerState: ActiveTimerState,
    onLaunchApp: (AppItem, android.view.View) -> Unit,
    onOpenMicroApp: (BuiltInMicroApp) -> Unit,
    onOpenAppLibrary: () -> Unit,
    onOpenControlCenter: () -> Unit,
    onOpenNotificationCenter: () -> Unit,
    onEnterLockScreen: () -> Unit,
    onMoveAppOrder: (String, Boolean) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleDockPin: (String) -> Unit,
    onOpenAndroidAppInfo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val surfaceTokens = LocalAetherSurfaceTokens.current
    val hasLightTop = settings.wallpaperPreset.hasLightUpperArea
    val labelColor = if (hasLightTop) Color(0xFF0E1422) else Color(0xFFF8FAFC)
    val subLabelColor = if (hasLightTop) Color(0xFF334155) else Color(0xFFCBD5E1)

    val itemsPerPage = (settings.gridDensity.columns * settings.gridDensity.rowsPerPage).coerceAtLeast(6)
    val pageCount = ceil(apps.size.toFloat() / itemsPerPage.toFloat()).toInt().coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    var swipeDownDelta by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (swipeDownDelta > 130f) {
                            swipeDownDelta = 0f
                            onOpenControlCenter()
                        } else {
                            swipeDownDelta = 0f
                        }
                    },
                    onDragCancel = { swipeDownDelta = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount > 0f) {
                            swipeDownDelta += dragAmount
                        }
                    }
                )
            }
            .testTag("home_screen_container")
    ) {
        val isCompactHeight = maxHeight < 640.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simulation & Quick Action Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_screen_sim_badge),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    ),
                    color = subLabelColor
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = onEnterLockScreen,
                        shape = RoundedCornerShape(50.dp),
                        color = surfaceTokens.translucentSurface,
                        modifier = Modifier.testTag("home_quick_lock_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Lock Screen",
                                tint = SapphireAccentLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Lock",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        onClick = onOpenControlCenter,
                        shape = RoundedCornerShape(50.dp),
                        color = surfaceTokens.translucentSurface,
                        modifier = Modifier.testTag("home_quick_control_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Control Center",
                                tint = CyanAether,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Controls",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Interactive Smart Widgets Row (Weather Glance & System/Timer Glance)
            if (!isCompactHeight) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Weather Widget Card
                    Surface(
                        onClick = { onOpenMicroApp(BuiltInMicroApp.WEATHER) },
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xD9101829),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.14f),
                                RoundedCornerShape(24.dp)
                            )
                            .testTag("widget_weather_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CUPERTINO SKY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanAether,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Filled.Cloud,
                                    contentDescription = null,
                                    tint = CyanAether,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "19°C",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Clear • H:22° L:13°",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }

                    // Telemetry & Chrono Widget Card
                    Surface(
                        onClick = {
                            if (timerState.isRunning) {
                                onOpenMicroApp(BuiltInMicroApp.CHRONO_TIMER)
                            } else {
                                onOpenMicroApp(BuiltInMicroApp.DIAGNOSTICS)
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xD9101829),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.14f),
                                RoundedCornerShape(24.dp)
                            )
                            .testTag("widget_telemetry_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (timerState.isRunning) "CHRONO ACTIVE" else "SYSTEM HEALTH",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (timerState.isRunning) AmberWarm else EmeraldActive,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = if (timerState.isRunning) AmberWarm else EmeraldActive,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = if (timerState.isRunning) {
                                    formatSecondsMinSec(timerState.remainingSeconds)
                                } else {
                                    "${telemetry.batteryPercent}% PWR"
                                },
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${telemetry.networkTransportLabel} • ${telemetry.batteryTempCelsius}°C",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFCBD5E1)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Adaptive App Icon Grid Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("home_app_grid_pager")
            ) { pageIndex ->
                val startIdx = pageIndex * itemsPerPage
                val pageApps = apps.drop(startIdx).take(itemsPerPage)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.gridDensity.columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = pageApps,
                        key = { it.id }
                    ) { appItem ->
                        HomeGridAppCell(
                            app = appItem,
                            settings = settings,
                            labelColor = labelColor,
                            onClick = { onLaunchApp(appItem, view) },
                            onMoveEarlier = { onMoveAppOrder(appItem.id, true) },
                            onMoveLater = { onMoveAppOrder(appItem.id, false) },
                            onToggleFavorite = { onToggleFavorite(appItem.id) },
                            onToggleDock = { onToggleDockPin(appItem.id) },
                            onOpenAppInfo = {
                                appItem.packageName?.let(onOpenAndroidAppInfo)
                            }
                        )
                    }
                }
            }

            // Search Pill & Page Dots Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pageCount) { idx ->
                        val selected = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .height(7.dp)
                                .width(if (selected) 20.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) labelColor else labelColor.copy(alpha = 0.35f)
                                )
                                .clickable {
                                    scope.launch { pagerState.animateScrollToPage(idx) }
                                }
                        )
                    }
                }

                // Spotlight-Inspired Global Search Pill
                Surface(
                    onClick = onOpenAppLibrary,
                    shape = RoundedCornerShape(50.dp),
                    color = surfaceTokens.translucentSurface,
                    modifier = Modifier
                        .border(1.dp, surfaceTokens.subtleBorder, RoundedCornerShape(50.dp))
                        .testTag("home_search_library_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search & App Library",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Search & App Library",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Premium Translucent Bottom Dock
            val dockApps = remember(apps, settings.dockAppIds) {
                val byId = apps.associateBy { it.id }
                settings.dockAppIds.mapNotNull { byId[it] }.ifEmpty { apps.take(4) }
            }

            Surface(
                shape = RoundedCornerShape(32.dp),
                color = surfaceTokens.dockBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(
                        width = 1.dp,
                        color = surfaceTokens.subtleBorder,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .testTag("home_bottom_dock")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockApps.forEach { dockApp ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { onLaunchApp(dockApp, view) }
                                .padding(4.dp)
                                .testTag("dock_app_${dockApp.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            AetherAppIconGraphic(
                                app = dockApp,
                                iconSize = 56.dp,
                                iconTheme = settings.iconTheme
                            )
                        }
                    }

                    // Permanent App Library Drawer Icon on Right End of Dock
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                            .clickable(onClick = onOpenAppLibrary)
                            .testTag("dock_app_library_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Apps,
                            contentDescription = "Open App Library",
                            tint = SapphireAccentLight,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeGridAppCell(
    app: AppItem,
    settings: UserSettings,
    labelColor: Color,
    onClick: () -> Unit,
    onMoveEarlier: () -> Unit,
    onMoveLater: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleDock: () -> Unit,
    onOpenAppInfo: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isFavorite = settings.favoriteAppIds.contains(app.id)
    val isPinnedToDock = settings.dockAppIds.contains(app.id)

    Box(contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { menuExpanded = true }
                )
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .testTag("grid_app_${app.id}")
        ) {
            AetherAppIconGraphic(
                app = app,
                iconSize = settings.iconSize.sizeDp.dp,
                iconTheme = settings.iconTheme
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = app.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp
                ),
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        // Long-Press Context Menu for Rearrangement, Favorites, and Dock Pinning
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Launch ${app.title}", fontWeight = FontWeight.SemiBold) },
                leadingIcon = { Icon(Icons.Filled.AppRegistration, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    onClick()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Move Earlier in Grid") },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                },
                onClick = {
                    menuExpanded = false
                    onMoveEarlier()
                }
            )
            DropdownMenuItem(
                text = { Text("Move Later in Grid") },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                onClick = {
                    menuExpanded = false
                    onMoveLater()
                }
            )
            DropdownMenuItem(
                text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                leadingIcon = {
                    Icon(
                        if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null
                    )
                },
                onClick = {
                    menuExpanded = false
                    onToggleFavorite()
                }
            )
            DropdownMenuItem(
                text = { Text(if (isPinnedToDock) "Unpin from Dock" else "Pin to Bottom Dock") },
                leadingIcon = { Icon(Icons.Filled.PushPin, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    onToggleDock()
                }
            )
            if (!app.isBuiltIn && app.packageName != null) {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Android App Info") },
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onOpenAppInfo()
                    }
                )
            }
        }
    }
}
