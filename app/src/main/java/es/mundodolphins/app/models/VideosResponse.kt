package es.mundodolphins.app.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

@Keep
data class VideosResponse(
    val videos: List<VideoResponse>,
    val totalCount: Int,
    val lastUpdated: String,
)

@Keep
data class VideoResponse(
    val duration: String?,
    val isPodcast: Boolean,
    @SerializedName("published_at")
    val publishedAt: String,
    val title: String,
    val url: String,
    val embeddable: Boolean,
) {
    val publishedTimestamp: Long
        get() = OffsetDateTime.parse(publishedAt).toInstant().toEpochMilli()
}
