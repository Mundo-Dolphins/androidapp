package es.mundodolphins.app.services

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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
}