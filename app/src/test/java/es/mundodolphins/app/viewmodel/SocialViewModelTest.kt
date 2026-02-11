package es.mundodolphins.app.viewmodel

import android.util.Log
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.repository.SocialRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SocialViewModelTest {
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
    fun `fetchSocialPostsSuspend sets SUCCESS when repository returns posts`() =
        runTest {
            val repository = mockk<SocialRepository>()
            coEvery { repository.getSocialPosts() } returns listOf(samplePost("new"))
            val viewModel = SocialViewModel(repository)

            val ok = viewModel.fetchSocialPostsSuspend()

            assertThat(ok).isTrue()
            assertThat(viewModel.status.value).isEqualTo(SocialViewModel.LoadStatus.SUCCESS)
            assertThat(viewModel.posts.value).hasSize(1)
            assertThat(viewModel.posts.value.first().id).isEqualTo("new")
        }

    @Test
    fun `fetchSocialPostsSuspend sets EMPTY when repository returns empty list`() =
        runTest {
            val repository = mockk<SocialRepository>()
            coEvery { repository.getSocialPosts() } returns emptyList()
            val viewModel = SocialViewModel(repository)

            val ok = viewModel.fetchSocialPostsSuspend()

            assertThat(ok).isTrue()
            assertThat(viewModel.status.value).isEqualTo(SocialViewModel.LoadStatus.EMPTY)
            assertThat(viewModel.posts.value).isEmpty()
        }

    @Test
    fun `fetchSocialPostsSuspend sets ERROR when repository throws`() =
        runTest {
            val repository = mockk<SocialRepository>()
            coEvery { repository.getSocialPosts() } throws RuntimeException("boom")
            val viewModel = SocialViewModel(repository)

            val ok = viewModel.fetchSocialPostsSuspend()

            assertThat(ok).isFalse()
            assertThat(viewModel.status.value).isEqualTo(SocialViewModel.LoadStatus.ERROR)
            assertThat(viewModel.posts.value).isEmpty()
        }

    private fun samplePost(id: String) =
        SocialUiModel(
            id = id,
            description = "Post description",
            profileName = "@mundodolphins.es - Mundo Dolphins",
            profileUrl = "https://bsky.app/profile/mundodolphins.es",
            postUrl = "https://bsky.app/profile/mundodolphins.es/post/$id",
            imageUrls = emptyList(),
            publishedAt = Instant.ofEpochMilli(1770805140000L),
            publishedOn = "11/02/2026 10:19",
            publishedTimestamp = 1770805140000L,
        )
}
