package es.mundodolphins.app.ui

import android.net.Uri
import java.util.Locale

sealed class Routes(
    val route: String,
) {
    data object Feed : Routes("feed")

    data object EpisodeView : Routes("more_information") {
        const val DEEP_LINK_URI_PATTERN = "mundodolphins://episode/{id}"
        const val APP_LINK_URI_PATTERN = "https://mundodolphins.es/app/episode/{id}"
        const val APP_LINK_URI_PATTERN_WWW = "https://www.mundodolphins.es/app/episode/{id}"
        private val APP_LINK_HOSTS =
            setOf("mundodolphins.es", "www.mundodolphins.es")
        private val APP_LINK_SCHEMES =
            setOf("https", "http")

        fun deepLinkUri(episodeId: Long) = "mundodolphins://episode/$episodeId"

        fun appLinkUri(episodeId: Long) = "https://mundodolphins.es/app/episode/$episodeId"

        private val APP_LINK_PATH_PATTERN = Regex("^/app/episode/([0-9]+)(?:/.*)?$")

        fun episodeIdFromUri(uri: Uri?): Long? =
            when {
                uri == null -> null
                uri.scheme?.lowercase(Locale.ROOT) == "mundodolphins" &&
                    uri.host?.lowercase(Locale.ROOT) == "episode" ->
                    uri.pathSegments.lastOrNull()?.toLongOrNull()
                APP_LINK_SCHEMES.contains(uri.scheme?.lowercase(Locale.ROOT)) &&
                    APP_LINK_HOSTS.contains(uri.host?.lowercase(Locale.ROOT)) -> {
                    val match = APP_LINK_PATH_PATTERN.matchEntire(uri.path.orEmpty()) ?: return null
                    match.groupValues.getOrNull(1)?.toLongOrNull()
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
