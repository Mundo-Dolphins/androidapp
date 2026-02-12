package es.mundodolphins.app.client

import android.os.Build
import com.google.gson.Gson
import es.mundodolphins.app.models.ArticlesResponse
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class ArticlesServiceMockWebServerTest {
    @Test
    fun `getArticles via MockWebServer returns list and correct path`() =
        runTest {
            // Ensure OkHttp uses JDK platform in unit tests to avoid android.util.Log calls
            System.setProperty("okhttp.platform", "jdk")

            val server = MockWebServer()
            server.start()

            val articles =
                listOf(
                    ArticlesResponse(
                        title = "Test Title",
                        author = "Author",
                        publishedDate = "2020-01-01T00:00:00Z",
                        content = "Content",
                    ),
                    ArticlesResponse(
                        title = "Another",
                        author = "Author 2",
                        publishedDate = "2021-02-02T00:00:00Z",
                        content = "More content",
                    ),
                )

            val articlesJson = Gson().toJson(articles)

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(articlesJson)
                    .addHeader("Content-Type", "application/json"),
            )

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(ArticlesService::class.java)

            val response = service.getArticles()

            // Verify response
            assertThat(response).isEqualTo(articles)
            assertThat(response).hasSize(2)
            assertThat(response[0].title).isEqualTo("Test Title")

            // Verify requested path
            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/articles.json")

            server.shutdown()
        }
}
