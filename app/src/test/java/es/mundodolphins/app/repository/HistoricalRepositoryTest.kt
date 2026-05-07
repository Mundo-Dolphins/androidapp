package es.mundodolphins.app.repository

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import es.mundodolphins.app.client.HistoricalService
import es.mundodolphins.app.models.historical.HistoricalGamesResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonStatsResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonsResponse
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response

class HistoricalRepositoryTest {
    private val gson = Gson()

    @Test
    fun `getSeasonDetail parses draft players and games from dynamic sections`() =
        runTest {
            val repository = HistoricalRepository(FakeHistoricalService())

            val seasonDetail = repository.getSeasonDetail(2024)

            assertThat(seasonDetail.title).isEqualTo("Temporada 2024")
            assertThat(seasonDetail.draftPlayers).hasSize(1)
            assertThat(seasonDetail.draftPlayers.first().valueFor("Nombre")).isEqualTo("Jaylen Wright")
            assertThat(seasonDetail.statsSections).hasSize(1)
            val firstRowYds =
                seasonDetail.statsSections
                    .first()
                    .tables
                    .first()
                    .rows
                    .first()["Yds"]
            assertThat(firstRowYds).isEqualTo("249")
            assertThat(seasonDetail.games).hasSize(1)
            val linescore = seasonDetail.games.first().linescore
            assertThat(linescore).hasSize(2)
            assertThat(linescore[0].team).isEqualTo("Jacksonville Jaguars")
            assertThat(linescore[0].finalScore).isEqualTo("17")
            assertThat(linescore[1].team).isEqualTo("Miami Dolphins")
            assertThat(linescore[1].finalScore).isEqualTo("20")
        }

    @Test
    fun `getGame returns linescore scoring summary and game info`() =
        runTest {
            val repository = HistoricalRepository(FakeHistoricalService())

            val game = repository.getGame(2024, "202409080mia")

            assertThat(game).isNotNull()
            assertThat(game?.linescore).hasSize(2)
            val firstPeriodScore =
                game
                    ?.linescore
                    ?.first()
                    ?.periods
                    ?.get("1")
            assertThat(firstPeriodScore).isEqualTo("7")
            assertThat(game?.scoringSummary).hasSize(1)
            assertThat(game?.scoringSummary?.first()?.description)
                .contains("Travis Etienne carrera de 1 yarda")
            assertThat(game?.gameInfo?.get("Estadio")).isEqualTo("Hard Rock Stadium")
        }

    @Test
    fun `getSeasons throws for unsuccessful service response`() =
        runTest {
            val repository = HistoricalRepository(ErrorHistoricalService())

            val failure = runCatching { repository.getSeasons() }.exceptionOrNull()

            assertThat(failure).isNotNull()
        }

    private inner class FakeHistoricalService : HistoricalService {
        override suspend fun getHistoricalSeasons(): Response<HistoricalSeasonsResponse> =
            Response.success(gson.fromJson(SEASONS_JSON, HistoricalSeasonsResponse::class.java))

        override suspend fun getHistoricalSeason(year: Int): Response<HistoricalSeasonResponse> =
            Response.success(gson.fromJson(SEASON_JSON, HistoricalSeasonResponse::class.java))

        override suspend fun getHistoricalSeasonStats(year: Int): Response<HistoricalSeasonStatsResponse> =
            Response.success(gson.fromJson(STATS_JSON, HistoricalSeasonStatsResponse::class.java))

        override suspend fun getHistoricalSeasonGames(year: Int): Response<HistoricalGamesResponse> =
            Response.success(gson.fromJson(GAMES_JSON, HistoricalGamesResponse::class.java))
    }

    private class ErrorHistoricalService : HistoricalService {
        override suspend fun getHistoricalSeasons(): Response<HistoricalSeasonsResponse> = Response.error(500, "boom".toResponseBody())

        override suspend fun getHistoricalSeason(year: Int): Response<HistoricalSeasonResponse> =
            Response.error(500, "boom".toResponseBody())

        override suspend fun getHistoricalSeasonStats(year: Int): Response<HistoricalSeasonStatsResponse> =
            Response.error(500, "boom".toResponseBody())

        override suspend fun getHistoricalSeasonGames(year: Int): Response<HistoricalGamesResponse> =
            Response.error(500, "boom".toResponseBody())
    }

    private companion object {
        const val SEASONS_JSON =
            """
            {
              "seasons": [
                {
                  "year": 2024,
                  "title": "Temporada 2024",
                  "urls": {
                    "season": "/api/historical/seasons/2024.json",
                    "stats": "/api/historical/seasons/2024/stats.json",
                    "games": "/api/historical/seasons/2024/games.json"
                  }
                }
              ]
            }
            """

        const val SEASON_JSON =
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
                    "PJ": "15",
                    "Yardas de carrera": "249"
                  }
                ],
                "estadísticas de la temporada": [
                  {
                    "id": "2024",
                    "title": "Estadísticas de la temporada",
                    "url": "/historia/stats/2024"
                  }
                ]
              }
            }
            """

        const val STATS_JSON =
            """
            {
              "sections": {
                "estadísticas avanzadas": {
                  "carrera avanzada": [
                    {
                      "Nombre": "Jaylen Wright",
                      "Yds": "249"
                    }
                  ]
                }
              }
            }
            """

        const val GAMES_JSON =
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
                        {
                          "Equipo": "Jacksonville Jaguars",
                          "1": "7",
                          "2": "10",
                          "3": "0",
                          "4": "0",
                          "Final": "17"
                        },
                        {
                          "Equipo": "Miami Dolphins",
                          "1": "0",
                          "2": "7",
                          "3": "7",
                          "4": "6",
                          "Final": "20"
                        }
                      ],
                      "scoring_summary": [
                        {
                          "Cuarto": "1",
                          "Equipo": "Jacksonville Jaguars",
                          "Descripción": "Travis Etienne carrera de 1 yarda (Cam Little kick)",
                          "Marcador visitante": "7",
                          "Marcador local": "0",
                          "Tiempo": "4:31"
                        }
                      ]
                    },
                    "Información del partido": {
                      "Estadio": "Hard Rock Stadium",
                      "Fecha": "8 de septiembre de 2024",
                      "Semana": "1"
                    }
                  }
                }
              ]
            }
            """
    }
}
