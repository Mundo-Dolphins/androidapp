package es.mundodolphins.app.client

import android.os.Build
import com.google.common.truth.Truth.assertThat
import es.mundodolphins.app.models.historical.HistoricalGameResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonsResponse
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
class HistoricalServiceMockWebServerTest {
    @Test
    fun `getHistoricalSeasons returns parsed seasons and correct path`() =
        runTest {
            val server = MockWebServer()
            server.start()
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "seasons": [
                            {
                              "year": 2024,
                              "title": "Temporada 2024",
                              "urls": {
                                "season": "/historical/seasons/2024.json",
                                "stats": "/historical/seasons/2024/stats.json",
                                "games": "/historical/seasons/2024/games.json"
                              }
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            )

            val service = createService(server)

            val response = service.getHistoricalSeasons()

            assertThat(response.isSuccessful).isTrue()
            assertThat(response.body()).isEqualTo(
                HistoricalSeasonsResponse(
                    seasons =
                        listOf(
                            es.mundodolphins.app.models.historical.HistoricalSeasonSummaryResponse(
                                year = 2024,
                                title = "Temporada 2024",
                            ),
                        ),
                ),
            )
            assertThat(server.takeRequest().path).isEqualTo("/historical/seasons.json")
            server.shutdown()
        }

    @Test
    fun `getHistoricalSeason parses dynamic sections and correct path`() =
        runTest {
            val server = MockWebServer()
            server.start()
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "sections": {
                            "jugadores seleccionados en el draft": [
                              {
                                "Nombre": "Jaylen Wright",
                                "Posición": "RB",
                                "Ronda": "4",
                                "Pick": "120",
                                "Universidad": "Tennessee",
                                "PJ": "15"
                              }
                            ]
                          }
                        }
                        """.trimIndent(),
                    ),
            )

            val service = createService(server)

            val response = service.getHistoricalSeason(2024)
            val body = response.body() ?: error("body should not be null")

            assertThat(response.isSuccessful).isTrue()
            assertThat(body).isInstanceOf(HistoricalSeasonResponse::class.java)
            assertThat(body.sections.keys).contains("jugadores seleccionados en el draft")
            assertThat(server.takeRequest().path).isEqualTo("/historical/seasons/2024.json")
            server.shutdown()
        }

    @Test
    fun `getHistoricalSeasonGames parses games and correct path`() =
        runTest {
            val server = MockWebServer()
            server.start()
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(
                        """
                        {
                          "games": [
                            {
                              "game_id": "202409080mia",
                              "title": "Jacksonville Jaguars vs Miami Dolphins",
                              "date": "2024-09-08",
                              "slug": "jaguars-vs-dolphins",
                              "sections": {
                                "Anotaciones": {
                                  "linescore": [
                                    { "Equipo": "Jacksonville Jaguars", "1": "7", "Final": "17" },
                                    { "Equipo": "Miami Dolphins", "1": "0", "Final": "20" }
                                  ],
                                  "scoring_summary": []
                                },
                                "Información del partido": {
                                  "Estadio": "Hard Rock Stadium"
                                }
                              }
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            )

            val service = createService(server)

            val response = service.getHistoricalSeasonGames(2024)
            val game = response.body()?.games?.firstOrNull()

            assertThat(response.isSuccessful).isTrue()
            assertThat(game).isInstanceOf(HistoricalGameResponse::class.java)
            assertThat(game?.gameId).isEqualTo("202409080mia")
            assertThat(game?.sections?.keys).contains("Anotaciones")
            assertThat(server.takeRequest().path).isEqualTo("/historical/seasons/2024/games.json")
            server.shutdown()
        }

    private fun createService(server: MockWebServer): HistoricalService =
        Retrofit
            .Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HistoricalService::class.java)
}
