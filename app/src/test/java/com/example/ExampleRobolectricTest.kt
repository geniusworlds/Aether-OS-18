package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.SettingsRepository
import com.example.data.aetherDataStore
import com.example.model.GridDensity
import com.example.model.ThemePreference
import com.example.model.WallpaperPreset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyAppNameResourceMatchesMetadata() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aether OS 18", appName)
    }

    @Test
    fun verifyHomeScreenDynamicCapsuleAndControlCenterFlow() {
        composeTestRule.waitForIdle()

        // Home screen & Dynamic Capsule should be visible
        composeTestRule.onNodeWithTag("aether_root_shell").assertIsDisplayed()
        composeTestRule.onNodeWithTag("dynamic_capsule_pill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_bottom_dock").assertIsDisplayed()

        // Expand Dynamic Capsule
        composeTestRule.onNodeWithTag("dynamic_capsule_pill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("capsule_play_pause_button").assertIsDisplayed()

        // Open Control Center via Home Quick Controls button
        composeTestRule.onNodeWithTag("home_quick_control_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("control_center_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cc_tile_flashlight").assertIsDisplayed()

        // Close Control Center
        composeTestRule.onNodeWithTag("close_control_center_button").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyLockScreenSimulationAndUnlockFlow() {
        composeTestRule.waitForIdle()

        // Enter Lock Screen Simulation from Home
        composeTestRule.onNodeWithTag("home_quick_lock_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("lock_screen_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithText("IN-APP LOCK SIMULATION • ANDROID SECURITY PRESERVED").assertIsDisplayed()

        // Toggle inline Lock Screen customizer
        composeTestRule.onNodeWithTag("lock_customize_toggle_button").performClick()
        composeTestRule.waitForIdle()

        // Unlock back to Home Screen
        composeTestRule.onNodeWithTag("unlock_home_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_screen_container").assertIsDisplayed()
    }

    @Test
    fun verifyNotificationCenterAndAppLibrarySearchFlow() {
        composeTestRule.waitForIdle()

        // Open Notification Center via top-left status bar
        composeTestRule.onNodeWithTag("status_bar_left_notifications").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("notification_center_overlay").assertIsDisplayed()

        // Clear Demo Notifications and verify Restore button appears
        composeTestRule.onNodeWithTag("clear_all_demo_notifications_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("restore_demo_notifications_button").assertIsDisplayed()

        // Restore Demo Notifications and close Notification Center
        composeTestRule.onNodeWithTag("restore_demo_notifications_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("close_notification_center_button").performClick()
        composeTestRule.waitForIdle()

        // Open App Library via Search Pill
        composeTestRule.onNodeWithTag("home_search_library_pill").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("app_library_overlay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("close_app_library_button").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyDataStoreSettingsPersistenceAndReset() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SettingsRepository(context.aetherDataStore)

        repo.setThemePreference(ThemePreference.DARK)
        repo.setWallpaperPreset(WallpaperPreset.PEARL_DUNE)
        repo.setGridDensity(GridDensity.COMPACT_4X5)

        val updated = repo.settingsFlow.first()
        assertEquals(ThemePreference.DARK, updated.themePreference)
        assertEquals(WallpaperPreset.PEARL_DUNE, updated.wallpaperPreset)
        assertEquals(GridDensity.COMPACT_4X5, updated.gridDensity)

        repo.resetToDefaults()
        val resetState = repo.settingsFlow.first()
        assertEquals(ThemePreference.SYSTEM, resetState.themePreference)
        assertEquals(WallpaperPreset.TITANIUM_SILK, resetState.wallpaperPreset)
        assertEquals(GridDensity.COMFORTABLE_4X4, resetState.gridDensity)
    }
}
