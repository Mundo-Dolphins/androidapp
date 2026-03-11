package es.mundodolphins.app.ui

import android.net.Uri

sealed class Routes(
    val route: String,
) {
    data object Feed : Routes("feed")

    data object EpisodeView : Routes("more_information") {
        const val DEEP_LINK_URI_PATTERN = "mundodolphins://episode/{id}"
        const val APP_LINK_URI_PATTERN = "https://mundodolphins.es/app/episode/{id}"

        fun deepLinkUri(episodeId: Long) = "mundodolphins://episode/$episodeId"

        fun appLinkUri(episodeId: Long) = "https://mundodolphins.es/app/episode/$episodeId"

        fun episodeIdFromUri(uri: Uri?): Long? =
            when {
                uri == null -> null
                uri.scheme == "mundodolphins" && uri.host == "episode" -> uri.lastPathSegment?.toLongOrNull()
                uri.scheme == "https" && uri.host == "mundodolphins.es" -> {
                    val segments = uri.pathSegments
                    if (segments.size >= 3 && segments[0] == "app" && segments[1] == "episode") {
                        segments[2].toLongOrNull()
                    } else {
                        null
                    }
                }

                else -> null
            }
    }

    data object UsefulLinks : Routes("useful_links")

    data object SeasonsList : Routes("seasons_list")

    data object SeasonView : Routes("seasons_view")

    data object Articles : Routes("articles")

    data object Article : Routes("article")

    data object Videos : Routes("videos")

    data object Social : Routes("social")
}
