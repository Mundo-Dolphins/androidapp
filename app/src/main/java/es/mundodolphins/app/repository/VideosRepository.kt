package es.mundodolphins.app.repository

import es.mundodolphins.app.client.VideosService
import es.mundodolphins.app.viewmodel.VideoUiModel
import es.mundodolphins.app.viewmodel.toVideoUiModel
import javax.inject.Inject

class VideosRepository
    @Inject
    constructor(
    private val videosService: VideosService,
    ) {
    suspend fun getVideos(): List<VideoUiModel> =
        videosService
            .getVideos()
            .videos
            .map { it.toVideoUiModel() }
            .sortedByDescending { it.publishedTimestamp }
}
