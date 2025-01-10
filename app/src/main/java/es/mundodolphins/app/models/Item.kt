package es.mundodolphins.app.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Item(
    val author: String,
    val categories: List<Any>,
    val content: String,
    val description: String,
    val enclosure: Enclosure,
    val guid: String,
    val link: String,
    val pubDate: String,
    val thumbnail: String,
    val title: String
) {
    val pubDateTime: LocalDateTime
        get() = LocalDateTime.parse(pubDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    val id
        get() = guid.replace(Regex("[^0-9]"), "")
}