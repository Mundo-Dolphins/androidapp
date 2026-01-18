package es.mundodolphins.app.client

import es.mundodolphins.app.models.EpisodeResponse
import retrofit2.Response

class FakeFeedService(
    private val episodes: List<EpisodeResponse> = emptyList(),
) : FeedService {
    override suspend fun getAllSeasons(): Response<List<String>> = Response.success(emptyList())

    override suspend fun getSeasonEpisodes(id: Int): Response<List<EpisodeResponse>> = Response.success(episodes)
}
