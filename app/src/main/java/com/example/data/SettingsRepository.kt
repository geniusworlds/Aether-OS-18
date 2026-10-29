package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AnimationIntensity
import com.example.model.ClockStyle
import com.example.model.GridDensity
import com.example.model.IconSizeOption
import com.example.model.IconThemeOption
import com.example.model.LockShortcutAction
import com.example.model.ThemePreference
import com.example.model.UserSettings
import com.example.model.WallpaperPreset
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.aetherDataStore: DataStore<Preferences> by preferencesDataStore(name = "aether_os_18_settings")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_preference")
        val WALLPAPER = stringPreferencesKey("wallpaper_preset")
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val LEFT_SHORTCUT = stringPreferencesKey("left_lock_shortcut")
        val RIGHT_SHORTCUT = stringPreferencesKey("right_lock_shortcut")
        val GRID_DENSITY = stringPreferencesKey("grid_density")
        val ICON_SIZE = stringPreferencesKey("icon_size")
        val ICON_THEME = stringPreferencesKey("icon_theme")
        val ANIMATION_INTENSITY = stringPreferencesKey("animation_intensity")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val GROUP_NOTIFS_BY_APP = booleanPreferencesKey("group_notifications_by_app")
        val FAVORITE_APPS = stringSetPreferencesKey("favorite_app_ids")
        val CUSTOM_APP_ORDER = stringPreferencesKey("custom_app_order_csv")
        val DOCK_APPS = stringPreferencesKey("dock_app_ids_csv")
    }

    val settingsFlow: Flow<UserSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val defaultSettings = UserSettings()
            val theme = prefs[Keys.THEME]?.let {
                runCatching { ThemePreference.valueOf(it) }.getOrNull()
            } ?: defaultSettings.themePreference

            val orderCsv = prefs[Keys.CUSTOM_APP_ORDER]
            val customOrder = if (orderCsv.isNullOrBlank()) {
                emptyList()
            } else {
                orderCsv.split(",").filter { it.isNotBlank() }
            }

            val dockCsv = prefs[Keys.DOCK_APPS]
            val dockIds = if (dockCsv.isNullOrBlank()) {
                defaultSettings.dockAppIds
            } else {
                dockCsv.split(",").filter { it.isNotBlank() }
            }

            UserSettings(
                themePreference = theme,
                wallpaperPreset = WallpaperPreset.fromId(prefs[Keys.WALLPAPER]),
                clockStyle = ClockStyle.fromId(prefs[Keys.CLOCK_STYLE]),
                leftLockShortcut = LockShortcutAction.fromId(
                    prefs[Keys.LEFT_SHORTCUT],
                    LockShortcutAction.FLASHLIGHT
                ),
                rightLockShortcut = LockShortcutAction.fromId(
                    prefs[Keys.RIGHT_SHORTCUT],
                    LockShortcutAction.CAMERA_APP
                ),
                gridDensity = GridDensity.fromId(prefs[Keys.GRID_DENSITY]),
                iconSize = IconSizeOption.fromId(prefs[Keys.ICON_SIZE]),
                iconTheme = IconThemeOption.fromId(prefs[Keys.ICON_THEME]),
                animationIntensity = AnimationIntensity.fromId(prefs[Keys.ANIMATION_INTENSITY]),
                hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
                groupNotificationsByApp = prefs[Keys.GROUP_NOTIFS_BY_APP] ?: false,
                favoriteAppIds = prefs[Keys.FAVORITE_APPS] ?: defaultSettings.favoriteAppIds,
                customAppOrderIds = customOrder,
                dockAppIds = dockIds
            )
        }

    suspend fun setThemePreference(preference: ThemePreference) {
        dataStore.edit { it[Keys.THEME] = preference.name }
    }

    suspend fun setWallpaperPreset(preset: WallpaperPreset) {
        dataStore.edit { it[Keys.WALLPAPER] = preset.id }
    }

    suspend fun setClockStyle(style: ClockStyle) {
        dataStore.edit { it[Keys.CLOCK_STYLE] = style.id }
    }

    suspend fun setLockShortcut(isLeft: Boolean, action: LockShortcutAction) {
        dataStore.edit {
            if (isLeft) it[Keys.LEFT_SHORTCUT] = action.id
            else it[Keys.RIGHT_SHORTCUT] = action.id
        }
    }

    suspend fun setGridDensity(density: GridDensity) {
        dataStore.edit { it[Keys.GRID_DENSITY] = density.id }
    }

    suspend fun setIconSize(size: IconSizeOption) {
        dataStore.edit { it[Keys.ICON_SIZE] = size.id }
    }

    suspend fun setIconTheme(theme: IconThemeOption) {
        dataStore.edit { it[Keys.ICON_THEME] = theme.id }
    }

    suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
        dataStore.edit { it[Keys.ANIMATION_INTENSITY] = intensity.id }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setGroupNotificationsByApp(grouped: Boolean) {
        dataStore.edit { it[Keys.GROUP_NOTIFS_BY_APP] = grouped }
    }

    suspend fun toggleFavoriteApp(appId: String) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITE_APPS] ?: UserSettings().favoriteAppIds
            prefs[Keys.FAVORITE_APPS] = if (current.contains(appId)) {
                current - appId
            } else {
                current + appId
            }
        }
    }

    suspend fun saveCustomAppOrder(orderedIds: List<String>) {
        dataStore.edit { prefs ->
            prefs[Keys.CUSTOM_APP_ORDER] = orderedIds.joinToString(",")
        }
    }

    suspend fun toggleDockApp(appId: String) {
        dataStore.edit { prefs ->
            val currentCsv = prefs[Keys.DOCK_APPS]
            val currentList = if (currentCsv.isNullOrBlank()) {
                UserSettings().dockAppIds
            } else {
                currentCsv.split(",").filter { it.isNotBlank() }
            }
            val updated = if (currentList.contains(appId)) {
                if (currentList.size > 2) currentList - appId else currentList
            } else {
                (currentList + appId).takeLast(4)
            }
            prefs[Keys.DOCK_APPS] = updated.joinToString(",")
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}
