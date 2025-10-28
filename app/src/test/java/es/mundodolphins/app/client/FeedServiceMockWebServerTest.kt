package es.mundodolphins.app.client

import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import es.mundodolphins.app.models.EpisodeResponse
import org.assertj.core.api.Assertions.assertThat

class FeedServiceMockWebServerTest {

    @Test
    fun `getAllSeasons via MockWebServer returns list and correct path`() = runTest {
        val server = MockWebServer()
        server.start()

        val seasons = listOf("season_1.json", "season_2.json")
        val seasonsJson = Gson().toJson(seasons)

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(seasonsJson)
                .addHeader("Content-Type", "application/json")
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(FeedService::class.java)

        val response = service.getAllSeasons()

        // Verify response
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isEqualTo(seasons)

        // Verify the requested path
        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/seasons.json")

        server.shutdown()
    }

    @Test
    fun `getSeasonEpisodes via MockWebServer returns episodes and correct path`() = runTest {
        val server = MockWebServer()
        server.start()

        val episodes = listOf(
            EpisodeResponse(
                dateAndTime = "2020-01-01T00:00:00Z",
                description = "desc",
                audio = "audio",
                imgMain = "main",
                imgMini = "mini",
                len = "00:10:00",
                link = "link",
                title = "title"
            )
        )

        val episodesJson = Gson().toJson(episodes)

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(episodesJson)
                .addHeader("Content-Type", "application/json")
        )

        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(FeedService::class.java)

        val response = service.getSeasonEpisodes(1)

        // Verify response
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).hasSize(episodes.size)
        assertThat(response.body()!![0].title).isEqualTo("title")

        // Verify the requested path
        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/season_1.json")

        server.shutdown()
    }
}
