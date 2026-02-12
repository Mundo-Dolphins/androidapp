package es.mundodolphins.app.client

import android.os.Build
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.S])
class VideosServiceMockWebServerTest {
    @Test
    fun `getVideos via MockWebServer parses response and hits correct path`() =
        runTest {
            System.setProperty("okhttp.platform", "jdk")

            val server = MockWebServer()
            server.start()

            val body =
                """
                {
                  "videos": [
                    {
                      "duration": "24:45",
                      "isPodcast": false,
                      "published_at": "2026-02-02T12:40:21-08:00",
                      "title": "¿Qué esperar de la defensa de Jeff Hafley?",
                      "url": "https://www.youtube.com/watch?v=8SUjlSJ-Cbs&pp=abc",
                      "embeddable": false
                    }
                  ],
                  "totalCount": 17,
                  "lastUpdated": "2026-02-10T15:08:37Z"
                }
                """.trimIndent()

            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(body)
                    .addHeader("Content-Type", "application/json"),
            )

            val retrofit =
                Retrofit
                    .Builder()
                    .baseUrl(server.url("/"))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(VideosService::class.java)
            val response = service.getVideos()

            assertThat(response.totalCount).isEqualTo(17)
            assertThat(response.videos).hasSize(1)
            assertThat(response.videos[0].title).isEqualTo("¿Qué esperar de la defensa de Jeff Hafley?")
            assertThat(response.videos[0].publishedTimestamp).isEqualTo(1770064821000L)

            val request = server.takeRequest()
            assertThat(request.path).isEqualTo("/videos-regular.json")

            server.shutdown()
        }
}
