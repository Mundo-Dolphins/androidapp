package es.mundodolphins.app.viewmodel

import es.mundodolphins.app.models.VideoResponse
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern

data class VideoUiModel(
    val title: String,
    val publishedAt: Instant,
    val publishedOn: String,
    val duration: String?,
    val url: String,
    val embeddable: Boolean,
    val videoId: String?,
    val thumbnailUrl: String?,
    val thumbnailFallbackUrl: String?,
    val publishedTimestamp: Long,
) {
    val embedUrl: String?
        get() = videoId?.let { "https://www.youtube.com/embed/$it?autoplay=1&playsinline=1" }
}

fun VideoResponse.toVideoUiModel(): VideoUiModel {
    val publishedInstant = Instant.ofEpochMilli(publishedTimestamp)
    val videoId = extractYoutubeVideoId(url)

    return VideoUiModel(
        title = title,
        publishedAt = publishedInstant,
        publishedOn = publishedInstant.atOffset(UTC).format(ofPattern("dd/MM/yyyy HH:mm")),
        duration = duration,
        url = url,
        embeddable = embeddable,
        videoId = videoId,
        thumbnailUrl = videoId?.let { buildMaxResThumbnailUrl(it) },
        thumbnailFallbackUrl = videoId?.let { buildHqThumbnailUrl(it) },
        publishedTimestamp = publishedTimestamp,
    )
}

fun extractYoutubeVideoId(url: String): String? {
    val uri = runCatching { URI(url.trim()) }.getOrNull()
    val host = uri?.host?.lowercase()?.removePrefix("www.")

    val candidate =
        when {
            uri == null || host.isNullOrBlank() -> null
            host == "youtu.be" -> uri.path.trim('/')
            host.endsWith("youtube.com") || host.endsWith("youtube-nocookie.com") -> {
                when {
                    uri.path == "/watch" -> getQueryParam(uri.rawQuery, "v")
                    uri.path.startsWith("/embed/") -> uri.path.removePrefix("/embed/").substringBefore('/')
                    uri.path.startsWith("/shorts/") -> uri.path.removePrefix("/shorts/").substringBefore('/')
                    else -> null
                }
            }

            else -> null
        }

    return sanitizeVideoId(candidate)
}

fun buildMaxResThumbnailUrl(videoId: String): String = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"

fun buildHqThumbnailUrl(videoId: String): String = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

private fun getQueryParam(
    query: String?,
    key: String,
): String? {
    if (query.isNullOrBlank()) {
        return null
    }

    return query
        .split("&")
        .firstOrNull { it.substringBefore("=") == key }
        ?.substringAfter("=", "")
        ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
}

private fun sanitizeVideoId(candidate: String?): String? {
    if (candidate.isNullOrBlank()) {
        return null
    }
    return Regex("""([A-Za-z0-9_-]{11})""").find(candidate)?.value
}
