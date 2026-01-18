package es.mundodolphins.app.ui

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.bar.AppBar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class MundoDolphinsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appBarShowsAppName() {
        // Set the content to the Composable under test
        composeTestRule.setContent {
            // Test the top AppBar directly to avoid heavy ViewModel / Firebase initialization
            AppBar()
        }

        // Verify that the app name text is displayed
        val appName = composeTestRule.activity.getString(R.string.app_name)
        composeTestRule.onNodeWithText(appName).assertExists()
    }
}
