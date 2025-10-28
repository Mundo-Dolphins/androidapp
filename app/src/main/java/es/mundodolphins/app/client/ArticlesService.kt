package es.mundodolphins.app.client

import es.mundodolphins.app.models.ArticlesResponse
import retrofit2.http.GET

interface ArticlesService {
    @GET("articles.json")
    suspend fun getArticles(): List<ArticlesResponse>
}
