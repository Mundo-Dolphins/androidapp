package es.mundodolphins.app.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import es.mundodolphins.app.R
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.ui.views.list.EpisodeRow
import io.mockk.mockk
import androidx.navigation.NavController
import java.time.Instant
import androidx.compose.ui.test.assertIsDisplayed

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class EpisodeRowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun episodeRow_displaysTitleAndMoreButton_and_clicksNavigate() {
        val navController = mockk<NavController>(relaxed = true)

        val episode = Episode(
            id = 123L,
            title = "Test Episode Title",
            description = "Short description",
            audio = "https://audio",
            published = Instant.ofEpochMilli(1744038703000),
            imgMain = "https://img",
            imgMini = null,
            len = "00:10:00",
            link = "https://link",
            season = 1,
        )

        composeTestRule.setContent {
            es.mundodolphins.app.ui.theme.MundoDolphinsTheme {
                EpisodeRow(episode = episode, navController = navController)
            }
        }

        // Verify title is displayed
        composeTestRule.onNodeWithText("Test Episode Title").assertIsDisplayed()

        // Verify 'More' button exists via string resource and is displayed
        val moreText = composeTestRule.activity.getString(R.string.more)
        composeTestRule.onNodeWithText(moreText).assertIsDisplayed()

        // Click the More button to ensure composable is interactable
        composeTestRule.onNodeWithText(moreText).performClick()
    }
}
