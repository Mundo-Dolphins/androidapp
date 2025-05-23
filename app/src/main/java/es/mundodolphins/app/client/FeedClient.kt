package es.mundodolphins.app.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FeedClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mundodolphins.es/api/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val service: FeedService by lazy {
        retrofit.create(FeedService::class.java)
    }
}