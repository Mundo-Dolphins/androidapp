package es.mundodolphins.app.viewmodel.player

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.media3.common.Player
import es.mundodolphins.app.services.AudioPlayerService
import es.mundodolphins.app.services.AudioPlayerService.AudioPlayerBinder
import es.mundodolphins.app.viewmodel.player.PlayerServiceHelper.IntentBuilder
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PlayerServiceHelperTest {
    private lateinit var playerServiceHelper: PlayerServiceHelper

    private lateinit var context: Context

    private lateinit var intentBuilder: IntentBuilder

    @Before
    fun setUp() {
        intentBuilder = mockk()
        context = mockk<Context>()
        playerServiceHelper = PlayerServiceHelper(intentBuilder)
    }

    @Test
    fun `should bind and start the service`() {
        val mp3Url = "https://example.com/audio.mp3"
        val currentPosition = 5000L
        val mockPlayer = mockk<Player>()
        val mockAudioPlayerService =
            mockk<AudioPlayerService> {
                every { getExoPlayer() } returns mockPlayer
            }
        val intent = mockk<Intent>()

        every { intentBuilder.buildIntent(context, mp3Url, currentPosition) } returns intent
        every {
            context.bindService(
                intent,
                any(),
                Context.BIND_AUTO_CREATE,
            )
        } returns true
        every { context.startForegroundService(intent) } returns mockk()

        playerServiceHelper.bindAndStartService(
            context,
            mp3Url,
            currentPosition,
        ) { exoPlayer, service ->
            assert(exoPlayer == mockPlayer)
            assert(service == mockAudioPlayerService)
        }

        verify { context.startForegroundService(intent) }
        verify { context.bindService(intent, any(), Context.BIND_AUTO_CREATE) }
    }

    @Test
    fun `should unbind and stop the service`() {
        val intent = mockk<Intent>()
        every { intentBuilder.buildIntent(context, any(), any()) } returns intent
        val mockPlayer = mockk<Player>()
        val mockAudioPlayerService =
            mockk<AudioPlayerService> {
                every { getExoPlayer() } returns mockPlayer
            }
        val audioPlayerBinder =
            mockk<AudioPlayerBinder> {
                every { getService() } returns mockAudioPlayerService
            }

        every { context.bindService(intent, any(), Context.BIND_AUTO_CREATE) } answers {
            val serviceConnection = arg<ServiceConnection>(1)
            serviceConnection.onServiceConnected(mockk(), audioPlayerBinder)
            true
        }
        every { context.startForegroundService(intent) } returns mockk()

        playerServiceHelper.bindAndStartService(
            context,
            "https://example.com/audio.mp3",
            0L,
        ) { _, _ -> }

        every { context.unbindService(any()) } just Runs
        every { context.stopService(any<Intent>()) } returns true

        playerServiceHelper.unbindAndStopService(context)

        verify { context.unbindService(any()) }
        verify { context.stopService(any()) }
    }
}
