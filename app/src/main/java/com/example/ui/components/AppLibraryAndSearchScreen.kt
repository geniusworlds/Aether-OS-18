package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.AppCategory
import com.example.model.AppItem
import com.example.model.UserSettings
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CyanAether
import com.example.ui.theme.SapphireAccentLight

@Composable
fun AppLibraryAndSearchOverlay(
    apps: List<AppItem>,
    settings: UserSettings,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLaunchApp: (AppItem, android.view.View) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val focusManager = LocalFocusManager.current

    val filteredApps = remember(apps, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            apps
        } else {
            apps.filter {
                it.title.lowercase().contains(q) ||
                    it.subtitle.lowercase().contains(q) ||
                    it.category.label.lowercase().contains(q)
            }
        }
    }

    val favoriteApps = remember(apps, settings.favoriteAppIds) {
        apps.filter { settings.favoriteAppIds.contains(it.id) }
    }

    val groupedByCategory = remember(filteredApps) {
        AppCategory.entries.mapNotNull { cat ->
            val list = filteredApps.filter { it.category == cat }
            if (list.isEmpty()) null else cat to list
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xEB070B14))
            .testTag("app_library_overlay"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.app_library_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${apps.size} Available Apps • Built-In & Installed Android Packages",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                    )
                }

                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B2436))
                        .testTag("close_app_library_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close App Library",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Global Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_apps_hint),
                        color = Color(0xFF94A3B8)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = CyanAether
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF141D2E),
                    unfocusedContainerColor = Color(0xFF121929),
                    focusedBorderColor = SapphireAccentLight,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_library_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Favorites Bar (shown when search query is empty)
                if (searchQuery.isBlank() && favoriteApps.isNotEmpty()) {
                    item(key = "favorites_shelf") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "PINNED FAVORITES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AmberWarm,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(
                                    items = favoriteApps,
                                    key = { "fav_${it.id}" }
                                ) { favApp ->
                                    Surface(
                                        onClick = {
                                            focusManager.clearFocus()
                                            onDismiss()
                                            onLaunchApp(favApp, view)
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xD9151E30),
                                        modifier = Modifier
                                            .border(
                                                1.dp,
                                                Color.White.copy(alpha = 0.14f),
                                                RoundedCornerShape(20.dp)
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            AetherAppIconGraphic(
                                                app = favApp,
                                                iconSize = 38.dp,
                                                iconTheme = settings.iconTheme
                                            )
                                            Column {
                                                Text(
                                                    text = favApp.title,
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        color = Color.White
                                                    )
                                                )
                                                Text(
                                                    text = favApp.subtitle,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Categorized Folders
                if (groupedByCategory.isEmpty()) {
                    item(key = "no_search_results") {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = Color(0xFF131B2B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "No matching applications",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                                )
                                Text(
                                    text = "Try searching for Weather, Chrono, Audio, Settings, or an installed Android app.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8))
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = groupedByCategory,
                        key = { it.first.name }
                    ) { (category, catApps) ->
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xD9131C2E),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(24.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.label.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanAether,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${catApps.size} apps",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF94A3B8)
                                        )
                                    )
                                }

                                catApps.forEach { app ->
                                    val isFav = settings.favoriteAppIds.contains(app.id)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                focusManager.clearFocus()
                                                onDismiss()
                                                onLaunchApp(app, view)
                                            }
                                            .padding(vertical = 6.dp, horizontal = 4.dp)
                                            .testTag("library_app_${app.id}"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            AetherAppIconGraphic(
                                                app = app,
                                                iconSize = 44.dp,
                                                iconTheme = settings.iconTheme
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = app.title,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = Color.White,
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = app.subtitle,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = Color(0xFF94A3B8)
                                                    ),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        IconButton(onClick = { onToggleFavorite(app.id) }) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Filled.Star else Icons.Filled.StarBorder,
                                                contentDescription = "Favorite",
                                                tint = if (isFav) AmberWarm else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
