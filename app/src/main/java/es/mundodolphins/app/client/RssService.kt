package es.mundodolphins.app.client

import es.mundodolphins.app.models.RssFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RssService {
    @GET("/v1/api.json")
    suspend fun getFeed(@Query("rss_url") rssUrl: String = "https://www.ivoox.com/feed_fg_f1601076_filtro_1.xml"): Response<RssFeed>
}