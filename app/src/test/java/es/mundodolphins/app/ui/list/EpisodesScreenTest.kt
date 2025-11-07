package es.mundodolphins.app.ui.list

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import es.mundodolphins.app.R
import es.mundodolphins.app.data.episodes.Episode
import es.mundodolphins.app.ui.views.list.EpisodesScreen
import es.mundodolphins.app.viewmodel.FakeEpisodesViewModel
import es.mundodolphins.app.viewmodel.EpisodesUiModel
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class EpisodesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun sampleEpisode(id: Long = 1L) = Episode(
        id = id,
        title = "Test Episode",
        description = "Description",
        audio = "https://example.com/audio.mp3",
        published = Instant.ofEpochMilli(1744038703000),
        imgMain = "",
        imgMini = "",
        len = "00:10:00",
        link = "https://example.com",
        season = 1,
    )

    @Test
    fun whenStatusSuccess_showsEpisodeTitleAndMoreButton() {
        val fake = FakeEpisodesViewModel(listOf(sampleEpisode()))

        composeTestRule.setContent {
            // Provide the fake directly
            EpisodesScreen(navController = rememberNavController(), model = fake as EpisodesUiModel)
        }

        // Verify title exists
        composeTestRule.onNodeWithText("Test Episode").assertIsDisplayed()

        // Verify "more" string is present (button text defined in resources)
        val more = composeTestRule.activity.getString(R.string.more)
        composeTestRule.onAllNodesWithText(more)[0].assertIsDisplayed()
    }
}
