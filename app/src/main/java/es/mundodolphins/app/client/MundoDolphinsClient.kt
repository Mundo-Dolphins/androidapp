package es.mundodolphins.app.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MundoDolphinsClient {
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://mundodolphins.es/api/")
            .addConverterFactory(
                GsonConverterFactory.create(),
            ).build()

    val feedService: FeedService by lazy {
        retrofit.create(FeedService::class.java)
    }

    val articlesService: ArticlesService by lazy {
        retrofit.create(ArticlesService::class.java)
    }

    val videosService: VideosService by lazy {
        retrofit.create(VideosService::class.java)
    }

    val socialService: SocialService by lazy {
        retrofit.create(SocialService::class.java)
    }
}
