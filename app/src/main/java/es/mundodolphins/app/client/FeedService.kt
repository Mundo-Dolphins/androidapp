package es.mundodolphins.app.client

import es.mundodolphins.app.models.Episode
import retrofit2.Response
import retrofit2.http.GET

interface FeedService {
    @GET("/data1.json")
    suspend fun getFeed(): Response<List<Episode>>
}