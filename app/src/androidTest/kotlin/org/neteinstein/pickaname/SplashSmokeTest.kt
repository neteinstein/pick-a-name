package org.neteinstein.pickaname

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end smoke test: the app must launch to the branded splash screen without crashing,
 * with real Koin DI wiring and a real (test-device-local) Room database/DataStore.
 *
 * [org.neteinstein.pickaname.presentation.splash.SplashViewModel] guarantees a minimum 900ms
 * splash duration before deciding where to navigate next, so asserting the splash content
 * immediately after launch is deterministic regardless of device speed, network availability,
 * or whether the on-device database already has data from a previous run.
 */
@RunWith(AndroidJUnit4::class)
class SplashSmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchesAndShowsSplashScreen() {
        val expectedAppName = composeTestRule.activity.getString(R.string.app_name)

        composeTestRule
            .onNodeWithText(expectedAppName)
            .assertIsDisplayed()
    }
}
