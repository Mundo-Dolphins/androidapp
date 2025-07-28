package es.mundodolphins.app.models

import java.time.OffsetDateTime


data class ArticlesResponse(
    val title: String,
    val author: String,
    val publishedDate: String,
    val content: String
) {
    val publishedTimestamp: Long
        get() = OffsetDateTime.parse(publishedDate).toInstant().toEpochMilli()
}