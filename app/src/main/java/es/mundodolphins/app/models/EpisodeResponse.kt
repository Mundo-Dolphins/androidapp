package es.mundodolphins.app.models

import androidx.annotation.Keep
import java.time.Instant

@Keep
data class EpisodeResponse(
    val dateAndTime: String,
    val description: String,
    val audio: String,
    val imgMain: String,
    val imgMini: String,
    val len: String,
    val link: String,
    val title: String
) {
    val pubDateTime: Instant
        get() = Instant.parse(dateAndTime)

    val id: Long
        get() = pubDateTime.toEpochMilli()
}