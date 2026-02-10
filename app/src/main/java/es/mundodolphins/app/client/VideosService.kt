package es.mundodolphins.app.client

import es.mundodolphins.app.models.VideosResponse
import retrofit2.http.GET

interface VideosService {
    @GET("videos-regular.json")
    suspend fun getVideos(): VideosResponse
}
