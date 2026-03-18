package es.mundodolphins.app.ui

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class RoutesEpisodeViewDeepLinkTest {
    @Test
    fun `episodeIdFromUri parses internal deep link`() {
        val uri = Uri.parse(Routes.EpisodeView.deepLinkUri(1234567890000L))

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1234567890000L)
    }

    @Test
    fun `episodeIdFromUri parses https app link`() {
        val uri = Uri.parse(Routes.EpisodeView.appLinkUri(1234567890000L))

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1234567890000L)
    }

    @Test
    fun `episodeIdFromUri parses https www subdomain link`() {
        val uri = Uri.parse("https://www.mundodolphins.es/app/episode/1769624287000")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1769624287000L)
    }

    @Test
    fun `episodeIdFromUri parses https link with trailing slash`() {
        val uri = Uri.parse("https://mundodolphins.es/app/episode/1769624287000/")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1769624287000L)
    }

    @Test
    fun `episodeIdFromUri parses https link with query parameters`() {
        val uri = Uri.parse("https://mundodolphins.es/app/episode/1769624287000?appAttempt=1")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1769624287000L)
    }

    @Test
    fun `episodeIdFromUri parses http fallback link`() {
        val uri = Uri.parse("http://mundodolphins.es/app/episode/1769624287000")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isEqualTo(1769624287000L)
    }

    @Test
    fun `episodeIdFromUri returns null for null uri`() {
        val parsed = Routes.EpisodeView.episodeIdFromUri(null)

        assertThat(parsed).isNull()
    }

    @Test
    fun `episodeIdFromUri returns null for non-app-link website url`() {
        val uri = Uri.parse("https://mundodolphins.es/podcast/some-slug/")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isNull()
    }

    @Test
    fun `episodeIdFromUri returns null for invalid app-link id`() {
        val uri = Uri.parse("https://mundodolphins.es/app/episode/not-a-number")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isNull()
    }

    @Test
    fun `episodeIdFromUri returns null for intent scheme uri`() {
        val uri = Uri.parse("intent://mundodolphins.es/app/episode/1769624287000#Intent;end")

        val parsed = Routes.EpisodeView.episodeIdFromUri(uri)

        assertThat(parsed).isNull()
    }
}
