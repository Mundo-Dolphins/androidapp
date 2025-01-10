package es.mundodolphins.app.client

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RssClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.rss2json.com/")
        .addConverterFactory(
            GsonConverterFactory.create())
        .build()

    val service: RssService by lazy {
        retrofit.create(RssService::class.java)
    }
}