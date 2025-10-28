package es.mundodolphins.app.services

import android.app.Service.START_STICKY
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class AudioPlayerServiceTest {
    @Test
    fun `should start the player service`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()

        assertThat(service).isNotNull()
        assertThat(service).isInstanceOf(AudioPlayerService::class.java)

        serviceController.startCommand(0, 0)

        val exoPlayer = service.getExoPlayer()
        assertThat(exoPlayer).isNotNull()
        serviceController.destroy()
        assertThat(exoPlayer.isPlaying).isFalse()
    }

    @Test
    fun `should handle null intent gracefully`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()
        val result = service.onStartCommand(null, 0, 0)

        // Ensure main looper is idle before assertion
        ShadowLooper.idleMainLooper()

        assertThat(result).isEqualTo(START_STICKY)
        serviceController.destroy()
    }

    @Test
    fun `should not crash when exoPlayer initialization fails`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()
        // Simula error en la inicialización del reproductor
        try {
            service.simulateExoPlayerError()
            assertThat(service.getExoPlayer().isPlaying).isFalse()
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }
        serviceController.destroy()
    }

    @Test
    fun `should release exoPlayer on service destroy`() {
        val serviceController = Robolectric.buildService(AudioPlayerService::class.java)
        val service = serviceController.create().get()
        val exoPlayer = service.getExoPlayer()
        serviceController.destroy()
        assertThat(exoPlayer.isPlaying).isFalse()
        assertThat(exoPlayer.isReleased).isTrue()
    }
}