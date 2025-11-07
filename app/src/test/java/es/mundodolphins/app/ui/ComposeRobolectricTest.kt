package es.mundodolphins.app.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import es.mundodolphins.app.R
import es.mundodolphins.app.ui.bar.AppBar
import es.mundodolphins.app.ui.theme.MundoDolphinsTheme

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ComposeRobolectricTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appBar_showsAppName() {
        val expected = ApplicationProvider.getApplicationContext<Context>().getString(R.string.app_name)

        composeTestRule.setContent {
            MundoDolphinsTheme {
                AppBar()
            }
        }

        composeTestRule.onNodeWithText(expected).assertIsDisplayed()
    }
}
