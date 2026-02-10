package es.mundodolphins.app.repository

import es.mundodolphins.app.client.VideosService
import es.mundodolphins.app.viewmodel.VideoUiModel
import es.mundodolphins.app.viewmodel.toVideoUiModel

class VideosRepository(
    private val videosService: VideosService,
) {
    suspend fun getVideos(): List<VideoUiModel> =
        videosService
            .getVideos()
            .videos
            .map { it.toVideoUiModel() }
            .sortedByDescending { it.publishedTimestamp }
}
