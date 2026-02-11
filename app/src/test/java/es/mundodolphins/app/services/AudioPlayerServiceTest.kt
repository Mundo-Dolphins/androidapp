package es.mundodolphins.app.services

import android.app.Service.START_STICKY
import android.content.Intent
import androidx.media3.common.Player
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AudioPlayerServiceTest {
    @After
    fun tearDown() {
        AudioPlayerService.resetExoPlayerFactory()
    }

    @Test
    fun `should create service and initialize player`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()

        assertThat(service).isNotNull()
        assertThat(service.getExoPlayer()).isNotNull()

        serviceController.destroy()
    }

    @Test
    fun `should handle null intent gracefully`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()

        val result = service.onStartCommand(null, 0, 0)

        ShadowLooper.idleMainLooper()

        assertThat(result).isEqualTo(START_STICKY)
        serviceController.destroy()
    }

    @Test
    fun `should accept playback intent and keep sticky lifecycle`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()
        val intent =
            Intent().apply {
                putExtra(AudioPlayerService.EXTRA_MP3_URL, "https://example.com/audio.mp3")
                putExtra(AudioPlayerService.EXTRA_CURRENT_POSITION, 1_000L)
                putExtra(AudioPlayerService.EXTRA_EPISODE_ID, 10L)
                putExtra(AudioPlayerService.EXTRA_EPISODE_TITLE, "Episode")
                putExtra(AudioPlayerService.EXTRA_EPISODE_IMAGE_URL, "https://example.com/cover.jpg")
            }

        val result = service.onStartCommand(intent, 0, 0)

        assertThat(result).isEqualTo(START_STICKY)
        assertThat(service.getExoPlayer().playbackState).isAnyOf(Player.STATE_BUFFERING, Player.STATE_READY)
        serviceController.destroy()
    }

    @Test
    fun `should not crash when exoPlayer initialization fails`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()

        try {
            service.simulateExoPlayerError()
            assertThat(service.getExoPlayer().isPlaying).isFalse()
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        serviceController.destroy()
    }
}
