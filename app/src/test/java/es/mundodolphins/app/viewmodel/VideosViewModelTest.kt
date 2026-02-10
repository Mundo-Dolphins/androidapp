package es.mundodolphins.app.viewmodel

import android.util.Log
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.repository.VideosRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

class VideosViewModelTest {
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
    fun `fetchVideosSuspend sets SUCCESS when repository returns videos`() =
        runTest {
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } returns listOf(sampleVideoUiModel("Video 1"))
            val viewModel = VideosViewModel(repository)

            val ok = viewModel.fetchVideosSuspend()

            assertThat(ok).isTrue()
            assertThat(viewModel.status.value).isEqualTo(VideosViewModel.LoadStatus.SUCCESS)
            assertThat(viewModel.videos.value).hasSize(1)
            assertThat(viewModel.videos.value.first().title).isEqualTo("Video 1")
        }

    @Test
    fun `fetchVideosSuspend sets EMPTY when repository returns empty list`() =
        runTest {
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } returns emptyList()
            val viewModel = VideosViewModel(repository)

            val ok = viewModel.fetchVideosSuspend()

            assertThat(ok).isTrue()
            assertThat(viewModel.status.value).isEqualTo(VideosViewModel.LoadStatus.EMPTY)
            assertThat(viewModel.videos.value).isEmpty()
        }

    @Test
    fun `fetchVideosSuspend sets ERROR when repository throws`() =
        runTest {
            val repository = mockk<VideosRepository>()
            coEvery { repository.getVideos() } throws RuntimeException("boom")
            val viewModel = VideosViewModel(repository)

            val ok = viewModel.fetchVideosSuspend()

            assertThat(ok).isFalse()
            assertThat(viewModel.status.value).isEqualTo(VideosViewModel.LoadStatus.ERROR)
            assertThat(viewModel.videos.value).isEmpty()
        }

    private fun sampleVideoUiModel(title: String) =
        VideoUiModel(
            title = title,
            publishedAt = Instant.ofEpochMilli(1770064821000L),
            publishedOn = "03/02/2026 20:40",
            duration = "24:45",
            url = "https://www.youtube.com/watch?v=8SUjlSJ-Cbs",
            embeddable = false,
            videoId = "8SUjlSJ-Cbs",
            thumbnailUrl = "https://img.youtube.com/vi/8SUjlSJ-Cbs/maxresdefault.jpg",
            thumbnailFallbackUrl = "https://img.youtube.com/vi/8SUjlSJ-Cbs/hqdefault.jpg",
            publishedTimestamp = 1770064821000L,
        )
}
