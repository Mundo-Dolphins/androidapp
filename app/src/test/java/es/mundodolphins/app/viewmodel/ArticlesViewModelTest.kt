package es.mundodolphins.app.viewmodel

import android.util.Log
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.client.ArticlesService
import es.mundodolphins.app.models.ArticlesResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlesViewModelTest {
    @Before
    fun setUp() {
        // Mock Android Log to avoid `not mocked` runtime errors in unit tests
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // nothing to unmock here
        unmockkStatic(Log::class)
    }

    @Test
    fun `fetchArticles should update stateflow when service returns articles`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            // Mock the service and inject it into the ViewModel
            val mockService = mockk<ArticlesService>()
            val published = "2025-01-01T00:00:00Z"
            val article =
                ArticlesResponse(
                    title = "Test Title",
                    author = "Author",
                    publishedDate = published,
                    content = "Content",
                )
            coEvery { mockService.getArticles() } returns listOf(article)

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.articles.value).isNotEmpty()
            assertThat(
                viewModel.articles.value
                    .first()
                    .title,
            ).isEqualTo("Test Title")

            coVerify(exactly = 1) { mockService.getArticles() }
        }

    @Test
    fun `fetchArticles should handle exceptions and keep empty list`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            coEvery { mockService.getArticles() } throws RuntimeException("Network error")

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.articles.value).isEmpty()

            coVerify(exactly = 1) { mockService.getArticles() }
        }

    @Test
    fun `getArticleByPublishedDate should return article when exists`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            val publishedA = "2025-01-01T00:00:00Z"
            val publishedB = "2025-01-02T00:00:00Z"
            val articleA =
                ArticlesResponse(
                    title = "Title A",
                    author = "Author A",
                    publishedDate = publishedA,
                    content = "Content A",
                )
            val articleB =
                ArticlesResponse(
                    title = "Title B",
                    author = "Author B",
                    publishedDate = publishedB,
                    content = "Content B",
                )
            coEvery { mockService.getArticles() } returns listOf(articleA, articleB)

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            val articleBTimestamp = requireNotNull(articleB.publishedTimestamp)
            val found = viewModel.getArticleByPublishedDate(articleBTimestamp)
            assertThat(found).isNotNull()
            assertThat(found?.title).isEqualTo("Title B")
        }

    @Test
    fun `getArticleByPublishedDate should return null when not found`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            val published = "2025-01-01T00:00:00Z"
            val article =
                ArticlesResponse(
                    title = "Title",
                    author = "Author",
                    publishedDate = published,
                    content = "Content",
                )
            coEvery { mockService.getArticles() } returns listOf(article)

            val viewModel = ArticlesViewModel(mockService)
            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            // Query for a timestamp that doesn't exist
            val missingTimestamp = requireNotNull(article.publishedTimestamp) + 1000L
            val result = viewModel.getArticleByPublishedDate(missingTimestamp)
            assertThat(result).isNull()
        }

    @Test
    fun `fetchArticles should result empty list when service returns empty`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            coEvery { mockService.getArticles() } returns emptyList()

            val viewModel = ArticlesViewModel(mockService)
            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(viewModel.articles.value).isEmpty()
        }

    @Test
    fun `fetchArticles should use cached articles on subsequent calls`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            val a = ArticlesResponse("A", "AuthA", "2025-01-01T00:00:00Z", "cA")
            val b = ArticlesResponse("B", "AuthB", "2025-01-02T00:00:00Z", "cB")

            // return A first call, B on second
            val counter = AtomicInteger(0)
            coEvery { mockService.getArticles() } answers {
                if (counter.incrementAndGet() == 1) listOf(a) else listOf(b)
            }

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value).hasSize(1)
            assertThat(
                viewModel.articles.value
                    .first()
                    .title,
            ).isEqualTo("A")

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value).hasSize(1)
            assertThat(
                viewModel.articles.value
                    .first()
                    .title,
            ).isEqualTo("A")
            coVerify(exactly = 1) { mockService.getArticles() }
        }

    @Test
    fun `fetchArticles should update articles when force is true`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            val a = ArticlesResponse("A", "AuthA", "2025-01-01T00:00:00Z", "cA")
            val b = ArticlesResponse("B", "AuthB", "2025-01-02T00:00:00Z", "cB")

            val counter = AtomicInteger(0)
            coEvery { mockService.getArticles() } answers {
                if (counter.incrementAndGet() == 1) listOf(a) else listOf(b)
            }

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value.first().title).isEqualTo("A")

            viewModel.fetchArticles(force = true)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value.first().title).isEqualTo("B")
            coVerify(exactly = 2) { mockService.getArticles() }
        }

    @Test
    fun `fetchArticles on forced exception should not clear existing articles`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val mockService = mockk<ArticlesService>()
            val first = ArticlesResponse("First", "A", "2025-01-01T00:00:00Z", "c1")
            val counter = AtomicInteger(0)
            coEvery { mockService.getArticles() } answers {
                if (counter.incrementAndGet() == 1) {
                    listOf(first)
                } else {
                    throw RuntimeException("boom")
                }
            }

            val viewModel = ArticlesViewModel(mockService)

            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value).hasSize(1)

            // second call throws, ensure previous content remains
            viewModel.fetchArticles(force = true)
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(viewModel.articles.value).hasSize(1)
            assertThat(
                viewModel.articles.value
                    .first()
                    .title,
            ).isEqualTo("First")
        }

    @Test
    fun `getArticleByPublishedDate should return first match when duplicates exist`() =
        runTest {
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val ts = "2025-01-01T00:00:00Z"
            val a1 = ArticlesResponse("A1", "X", ts, "c1")
            val a2 = ArticlesResponse("A2", "Y", ts, "c2")
            val mockService = mockk<ArticlesService>()
            coEvery { mockService.getArticles() } returns listOf(a1, a2)

            val viewModel = ArticlesViewModel(mockService)
            viewModel.fetchArticles()
            testDispatcher.scheduler.advanceUntilIdle()

            val a1Timestamp = requireNotNull(a1.publishedTimestamp)
            val found = viewModel.getArticleByPublishedDate(a1Timestamp)
            assertThat(found).isNotNull()
            // should return first matching article
            assertThat(found?.title).isEqualTo("A1")
        }
}
