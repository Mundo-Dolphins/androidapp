package es.mundodolphins.app.client

import es.mundodolphins.app.models.EpisodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedService {
    @GET("seasons.json")
    suspend fun getAllSeasons(): Response<List<String>>

    @GET("season_{id}.json")
    suspend fun getSeasonEpisodes(
        @Path(value = "id") id: Int,
    ): Response<List<EpisodeResponse>>
}
