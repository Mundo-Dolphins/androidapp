package es.mundodolphins.app.viewmodel

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import es.mundodolphins.app.client.HistoricalService
import es.mundodolphins.app.models.historical.HistoricalGamesResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonStatsResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonsResponse
import es.mundodolphins.app.repository.HistoricalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricalViewModelsTest {
    private val gson = Gson()

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `historical seasons viewmodel loads seasons successfully`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            val repository = HistoricalRepository(FakeHistoricalService())
            val viewModel = HistoricalSeasonsViewModel(repository, dispatcher)

            viewModel.loadSeasons()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertThat(state).isInstanceOf(HistoricalUiState.Success::class.java)
            val seasons = (state as HistoricalUiState.Success).data
            assertThat(seasons).hasSize(1)
            assertThat(seasons.first().year).isEqualTo(2024)
        }

    @Test
    fun `game detail viewmodel exposes error when game is missing`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            val repository = HistoricalRepository(EmptyGamesHistoricalService())
            val viewModel = GameDetailViewModel(repository, dispatcher)

            viewModel.loadGame(year = 2024, gameId = "missing")
            advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(HistoricalUiState.Error::class.java)
        }

    @Test
    fun `season detail viewmodel exposes draft and games`() =
        runTest {
            val dispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            val repository = HistoricalRepository(FakeHistoricalService())
            val viewModel = SeasonDetailViewModel(repository, dispatcher)

            viewModel.loadSeason(2024)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertThat(state).isInstanceOf(HistoricalUiState.Success::class.java)
            val season = (state as HistoricalUiState.Success).data
            assertThat(season.draftPlayers).hasSize(1)
            assertThat(season.statsSections).hasSize(1)
            assertThat(season.games).hasSize(1)
        }

    private inner class FakeHistoricalService : HistoricalService {
        override suspend fun getHistoricalSeasons(): Response<HistoricalSeasonsResponse> =
            Response.success(gson.fromJson(SEASONS_JSON, HistoricalSeasonsResponse::class.java))

        override suspend fun getHistoricalSeason(year: Int): Response<HistoricalSeasonResponse> =
            Response.success(gson.fromJson(SEASON_JSON, HistoricalSeasonResponse::class.java))

        override suspend fun getHistoricalSeasonByUrl(url: String): Response<HistoricalSeasonResponse> = getHistoricalSeason(2024)

        override suspend fun getHistoricalSeasonStats(year: Int): Response<HistoricalSeasonStatsResponse> =
            Response.success(gson.fromJson(STATS_JSON, HistoricalSeasonStatsResponse::class.java))

        override suspend fun getHistoricalSeasonStatsByUrl(url: String): Response<HistoricalSeasonStatsResponse> =
            getHistoricalSeasonStats(2024)

        override suspend fun getHistoricalSeasonGames(year: Int): Response<HistoricalGamesResponse> =
            Response.success(gson.fromJson(GAMES_JSON, HistoricalGamesResponse::class.java))

        override suspend fun getHistoricalSeasonGamesByUrl(url: String): Response<HistoricalGamesResponse> = getHistoricalSeasonGames(2024)
    }

    private inner class EmptyGamesHistoricalService : HistoricalService {
        override suspend fun getHistoricalSeasons(): Response<HistoricalSeasonsResponse> =
            Response.success(gson.fromJson(SEASONS_JSON, HistoricalSeasonsResponse::class.java))

        override suspend fun getHistoricalSeason(year: Int): Response<HistoricalSeasonResponse> =
            Response.success(gson.fromJson(SEASON_JSON, HistoricalSeasonResponse::class.java))

        override suspend fun getHistoricalSeasonByUrl(url: String): Response<HistoricalSeasonResponse> = getHistoricalSeason(2024)

        override suspend fun getHistoricalSeasonStats(year: Int): Response<HistoricalSeasonStatsResponse> =
            Response.success(gson.fromJson(STATS_JSON, HistoricalSeasonStatsResponse::class.java))

        override suspend fun getHistoricalSeasonStatsByUrl(url: String): Response<HistoricalSeasonStatsResponse> =
            getHistoricalSeasonStats(2024)

        override suspend fun getHistoricalSeasonGames(year: Int): Response<HistoricalGamesResponse> =
            Response.success(HistoricalGamesResponse(emptyList()))

        override suspend fun getHistoricalSeasonGamesByUrl(url: String): Response<HistoricalGamesResponse> = getHistoricalSeasonGames(2024)
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
                    "PJ": "15"
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
            """
    }
}
