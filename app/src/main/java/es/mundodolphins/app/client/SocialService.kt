package es.mundodolphins.app.client

import es.mundodolphins.app.models.SocialPostResponse
import retrofit2.http.GET

interface SocialService {
    @GET("social.json")
    suspend fun getSocialPosts(): List<SocialPostResponse>
}
