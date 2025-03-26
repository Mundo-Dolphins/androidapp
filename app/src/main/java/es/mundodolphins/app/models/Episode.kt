package es.mundodolphins.app.models

import androidx.annotation.Keep
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern

@Keep
data class Episode(
    val dateAndTime: String,
    val description: String,
    val audio: String,
    val imgMain: String,
    val imgMini: String,
    val len: String,
    val link: String,
    val title: String
) {
    private val pubDateTime: Instant
        get() = Instant.parse(dateAndTime)

    val id: Long
        get() = pubDateTime.toEpochMilli()

    val publishedOn: String
        get() = pubDateTime.atOffset(UTC).format(ofPattern("dd/MM/yyyy HH:mm"))
}