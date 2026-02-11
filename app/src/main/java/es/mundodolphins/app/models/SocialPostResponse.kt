package es.mundodolphins.app.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

@Keep
data class SocialPostResponse(
    val id: String,
    val stype: Int,
    @SerializedName("PublishedOn")
    val publishedOn: String,
    @SerializedName("BlueSkyPost")
    val blueSkyPost: BlueSkyPostResponse?,
    @SerializedName("InstagramPost")
    val instagramPost: InstagramPostResponse?,
) {
    val publishedTimestamp: Long
        get() = OffsetDateTime.parse(publishedOn).toInstant().toEpochMilli()
}

@Keep
data class BlueSkyPostResponse(
    @SerializedName("BskyURI")
    val bskyUri: String,
    @SerializedName("BskyCID")
    val bskyCid: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("BskyProfileURI")
    val bskyProfileUri: String,
    @SerializedName("BskyProfile")
    val bskyProfile: String,
    @SerializedName("BskyPost")
    val bskyPost: String,
    @SerializedName("Image")
    val image: String? = null,
    @SerializedName("Images")
    val images: List<BlueSkyImageResponse>? = null,
    @SerializedName("Media")
    val media: List<BlueSkyImageResponse>? = null,
)

@Keep
data class InstagramPostResponse(
    @SerializedName("URL")
    val url: String,
    @SerializedName("Description")
    val description: String,
    @SerializedName("SvgPath")
    val svgPath: String,
)

@Keep
data class BlueSkyImageResponse(
    @SerializedName("URL")
    val url: String? = null,
    @SerializedName("Uri")
    val uri: String? = null,
    @SerializedName("Fullsize")
    val fullsize: String? = null,
    @SerializedName("Thumbnail")
    val thumbnail: String? = null,
    @SerializedName("Thumb")
    val thumb: String? = null,
)
