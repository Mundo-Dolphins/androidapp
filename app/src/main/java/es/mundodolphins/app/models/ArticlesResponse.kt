package es.mundodolphins.app.models

import androidx.annotation.Keep
import java.time.OffsetDateTime

@Keep
data class ArticlesResponse(
    val title: String,
    val author: String,
    val publishedDate: String,
    val content: String,
) {
    val publishedTimestamp: Long
        get() = OffsetDateTime.parse(publishedDate).toInstant().toEpochMilli()
}
