package es.mundodolphins.app.ui.videos

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.R
import es.mundodolphins.app.repository.VideosRepository
import es.mundodolphins.app.ui.views.videos.VideosScreen
import es.mundodolphins.app.viewmodel.VideoUiModel
import es.mundodolphins.app.viewmodel.VideosViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class VideosScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns false
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun whenStatusSuccess_showsVideoAnd_clickOpensYoutubeIntent() =
        runTest {
            val url = "https://www.youtube.com/watch?v=8SUjlSJ-Cbs"
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } returns listOf(sampleVideoUiModel(url = url))
            val viewModel = VideosViewModel(repository)
            viewModel.fetchVideosSuspend()

            composeTestRule.setContent {
                VideosScreen(model = viewModel)
            }

            composeTestRule.onNodeWithText("Test Video").assertIsDisplayed()
            composeTestRule.onNodeWithText("Test Video").performClick()

            val startedIntent = shadowOf(composeTestRule.activity).nextStartedActivity
            assertThat(startedIntent).isNotNull()
            assertThat(startedIntent.action).isEqualTo(Intent.ACTION_VIEW)
            assertThat(startedIntent.dataString).isEqualTo(url)
        }

    @Test
    fun whenStatusEmpty_showsEmptyMessage() =
        runTest {
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } returns emptyList()
            val viewModel = VideosViewModel(repository)
            viewModel.fetchVideosSuspend()

            composeTestRule.setContent {
                VideosScreen(model = viewModel)
            }

            val expected = composeTestRule.activity.getString(R.string.videos_empty)
            composeTestRule.onNodeWithText(expected).assertIsDisplayed()
        }

    @Test
    fun whenStatusError_showsErrorAndRetry() =
        runTest {
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } throws RuntimeException("network")
            val viewModel = VideosViewModel(repository)
            viewModel.fetchVideosSuspend()

            composeTestRule.setContent {
                VideosScreen(model = viewModel)
            }

            val errorText = composeTestRule.activity.getString(R.string.videos_error)
            val retryText = composeTestRule.activity.getString(R.string.retry)
            composeTestRule.onNodeWithText(errorText).assertIsDisplayed()
            composeTestRule.onNodeWithText(retryText).assertIsDisplayed()
        }

    private fun sampleVideoUiModel(url: String) =
        VideoUiModel(
            title = "Test Video",
            publishedAt = Instant.ofEpochMilli(1770064821000L),
            publishedOn = "03/02/2026 20:40",
            duration = "24:45",
            url = url,
            embeddable = false,
            videoId = "8SUjlSJ-Cbs",
            thumbnailUrl = "https://img.youtube.com/vi/8SUjlSJ-Cbs/maxresdefault.jpg",
            thumbnailFallbackUrl = "https://img.youtube.com/vi/8SUjlSJ-Cbs/hqdefault.jpg",
            publishedTimestamp = 1770064821000L,
        )
}
