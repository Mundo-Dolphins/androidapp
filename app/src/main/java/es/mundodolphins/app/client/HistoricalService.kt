package es.mundodolphins.app.client

import es.mundodolphins.app.models.historical.HistoricalGamesResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonStatsResponse
import es.mundodolphins.app.models.historical.HistoricalSeasonsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HistoricalService {
    @GET("historical/seasons.json")
    suspend fun getHistoricalSeasons(): Response<HistoricalSeasonsResponse>

    @GET("historical/seasons/{year}.json")
    suspend fun getHistoricalSeason(
        @Path("year") year: Int,
    ): Response<HistoricalSeasonResponse>

    @GET("historical/seasons/{year}/stats.json")
    suspend fun getHistoricalSeasonStats(
        @Path("year") year: Int,
    ): Response<HistoricalSeasonStatsResponse>

    @GET("historical/seasons/{year}/games.json")
    suspend fun getHistoricalSeasonGames(
        @Path("year") year: Int,
    ): Response<HistoricalGamesResponse>
}
