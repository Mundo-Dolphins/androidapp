package es.mundodolphins.app.models

import java.time.Instant.ofEpochSecond
import java.time.LocalDateTime
import java.time.LocalDateTime.ofInstant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern

data class Episode(
    val date: String,
    val description: String,
    val audio: String,
    val imgMain: String,
    val imgMini: String,
    val len: String,
    val link: String,
    val title: String
) {
    val id: Int
        get() = date.toInt()

    val pubDateTime: LocalDateTime
        get() = ofInstant(ofEpochSecond(date.toLong()), UTC)

    val publishedOn: String
        get() = pubDateTime.format(ofPattern("dd/MM/yyyy HH:mm"))
}