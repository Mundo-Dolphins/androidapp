package es.mundodolphins.app.viewmodel

import android.util.Log
import com.google.gson.Gson
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.client.ArticlesService
import es.mundodolphins.app.models.ArticlesResponse
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticlesViewModelMockWebServerTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns false
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `fetchArticlesSuspend should update state when server returns data`() = runTest {
        val server = MockWebServer()
        server.start()

        val articles = listOf(
            ArticlesResponse("Title 1", "Author 1", "2025-01-01T00:00:00Z", "Content 1"),
            ArticlesResponse("Title 2", "Author 2", "2025-01-02T00:00:00Z", "Content 2")
        )
        val body = Gson().toJson(articles)

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json")
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ArticlesService::class.java)
        val viewModel = ArticlesViewModel(service)

        val ok = viewModel.fetchArticlesSuspend()

        assertThat(ok).isTrue()
        assertThat(viewModel.articles.value).hasSize(2)
        assertThat(viewModel.articles.value[0].title).isEqualTo("Title 1")

        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/articles.json")

        server.shutdown()
    }

    @Test
    fun `fetchArticlesSuspend should return false and not update state on server error`() = runTest {
        val server = MockWebServer()
        server.start()

        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("server error")
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ArticlesService::class.java)
        val viewModel = ArticlesViewModel(service)

        val ok = viewModel.fetchArticlesSuspend()

        assertThat(ok).isFalse()
        assertThat(viewModel.articles.value).isEmpty()

        server.shutdown()
    }
}
