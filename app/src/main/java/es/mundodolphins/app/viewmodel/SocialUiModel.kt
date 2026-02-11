package es.mundodolphins.app.viewmodel

import es.mundodolphins.app.models.SocialPostResponse
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ofPattern

data class SocialUiModel(
    val id: String,
    val description: String,
    val profileName: String,
    val profileUrl: String,
    val postUrl: String,
    val imageUrls: List<String>,
    val publishedAt: Instant,
    val publishedOn: String,
    val publishedTimestamp: Long,
)

fun SocialPostResponse.toSocialUiModel(): SocialUiModel? {
    return blueSkyPost
        ?.takeIf { it.bskyPost.isNotBlank() && it.description.isNotBlank() }
        ?.let { post ->
            val publishedInstant = Instant.ofEpochMilli(publishedTimestamp)
            SocialUiModel(
                id = id,
                description = post.description,
                profileName = post.bskyProfile,
                profileUrl = post.bskyProfileUri,
                postUrl = post.bskyPost,
                imageUrls =
                    buildList {
                        post.image?.takeIf { it.isNotBlank() }?.let(::add)
                        post.images.orEmpty().forEach { image ->
                            image.url?.takeIf { it.isNotBlank() }?.let(::add)
                            image.uri?.takeIf { it.isNotBlank() }?.let(::add)
                            image.fullsize?.takeIf { it.isNotBlank() }?.let(::add)
                            image.thumbnail?.takeIf { it.isNotBlank() }?.let(::add)
                            image.thumb?.takeIf { it.isNotBlank() }?.let(::add)
                        }
                        post.media.orEmpty().forEach { image ->
                            image.url?.takeIf { it.isNotBlank() }?.let(::add)
                            image.uri?.takeIf { it.isNotBlank() }?.let(::add)
                            image.fullsize?.takeIf { it.isNotBlank() }?.let(::add)
                            image.thumbnail?.takeIf { it.isNotBlank() }?.let(::add)
                            image.thumb?.takeIf { it.isNotBlank() }?.let(::add)
                        }
                    }.distinct(),
                publishedAt = publishedInstant,
                publishedOn = publishedInstant.atOffset(UTC).format(ofPattern("dd/MM/yyyy HH:mm")),
                publishedTimestamp = publishedTimestamp,
            )
        }
}
