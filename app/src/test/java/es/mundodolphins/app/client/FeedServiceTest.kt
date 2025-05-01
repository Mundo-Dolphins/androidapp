package es.mundodolphins.app.client

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedServiceTest {
    private lateinit var mockWebServer: MockWebServer

    private lateinit var service: FeedService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeedService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get all seasons`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse().apply {
                setBody("[\"season_1\",\"season_2\"]")
            }
        )

        // When
        val response = service.getAllSeasons()

        // Then
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).hasSize(2)
        assertThat(response.body()).containsExactly("season_1", "season_2")

        assertThat(mockWebServer.takeRequest().path).isEqualTo("/seasons.json")
    }

    @Test
    fun `get season episodes`() = runTest {
        // Given
        mockWebServer.enqueue(MockResponse().apply {
            setBody(
                """
                [
                    {
                        "dateAndTime": "2025-03-27T18:26:22Z",
                        "description": "Episode 1 description",
                        "audio": "https://www.audio.com/1.mp3",
                        "imgMain": "https://www.image.com/1.jpg",
                        "imgMini": "",
                        "len": "01:29:11",
                        "link": "https://www.episode.com/1",
                        "title": "Episode 1"
                    },
                    {
                        "dateAndTime": "2025-02-20T15:44:39Z",
                        "description": "Episode 2 description",
                        "audio": "https://www.audio.com/2.mp3",
                        "imgMain": "https://www.image.com/2.jpg",
                        "imgMini": "",
                        "len": "00:58:12",
                        "link": "https://www.episode.com/2",
                        "title": "Episode 2"
                    }
                ]   
                """.trimIndent()
            )
        })
        val expectedId = 1

        // When
        val response = service.getSeasonEpisodes(expectedId)

        // Then
        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).hasSize(2)
        assertThat(response.body()!![0].id).isEqualTo(1743099982000)
        assertThat(response.body()!![0].title).isEqualTo("Episode 1")
        assertThat(response.body()!![1].id).isEqualTo(1740066279000)
        assertThat(response.body()!![1].title).isEqualTo("Episode 2")

        assertThat(mockWebServer.takeRequest().path).isEqualTo("/season_1.json")
    }
}