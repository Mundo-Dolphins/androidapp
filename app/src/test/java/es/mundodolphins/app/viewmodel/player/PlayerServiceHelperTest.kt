package es.mundodolphins.app.viewmodel.player

import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PlayerServiceHelperTest {
    private lateinit var playerServiceHelper: PlayerServiceHelper
    private lateinit var context: Context
    private lateinit var intentBuilder: PlayerServiceHelper.IntentBuilder
    private lateinit var mediaControllerFactory: PlayerServiceHelper.MediaControllerFactory
    private lateinit var foregroundStarter: PlayerServiceHelper.ForegroundStarter
    private lateinit var sessionTokenFactory: PlayerServiceHelper.SessionTokenFactory
    private lateinit var controllerReleaser: PlayerServiceHelper.ControllerReleaser

    @Before
    fun setUp() {
        intentBuilder = mockk()
        context = mockk(relaxed = true)
        mediaControllerFactory = mockk()
        foregroundStarter = mockk()
        sessionTokenFactory = mockk()
        controllerReleaser = mockk()
        playerServiceHelper =
            PlayerServiceHelper(
                intentBuilder,
                mediaControllerFactory,
                foregroundStarter,
                sessionTokenFactory,
                controllerReleaser,
            )
    }

    @Test
    fun `should build media controller and connect`() {
        val request =
            PlayerServiceHelper.PlaybackRequest(
                episodeId = 1L,
                mp3Url = "https://example.com/audio.mp3",
                currentPosition = 5000L,
                title = "Episode",
                artworkUrl = "https://example.com/cover.jpg",
            )
        val intent = mockk<android.content.Intent>()
        val controller = mockk<MediaController>()
        val future = mockk<ListenableFuture<MediaController>>()
        val sessionToken = mockk<SessionToken>()

        every { intentBuilder.buildIntent(context, request) } returns intent
        every { foregroundStarter.start(context, intent) } just runs
        every { sessionTokenFactory.build(context) } returns sessionToken
        every { mediaControllerFactory.build(context, sessionToken) } returns future
        every { controllerReleaser.release(any()) } just runs
        every { future.addListener(any(), any()) } answers {
            firstArg<Runnable>().run()
        }
        every { future.isDone } returns true
        every { future.get() } returns controller

        playerServiceHelper.bindAndStartService(context, request) {}

        verify { intentBuilder.buildIntent(context, request) }
        verify { foregroundStarter.start(context, intent) }
        verify { sessionTokenFactory.build(context) }
        verify { mediaControllerFactory.build(context, sessionToken) }
        verify { future.get() }
    }

    @Test
    fun `should release controller and stop service`() {
        val request =
            PlayerServiceHelper.PlaybackRequest(
                episodeId = 1L,
                mp3Url = "https://example.com/audio.mp3",
                currentPosition = 0L,
                title = "Episode",
                artworkUrl = null,
            )
        val intent = mockk<android.content.Intent>()
        val future = mockk<ListenableFuture<MediaController>>()
        val sessionToken = mockk<SessionToken>()

        every { intentBuilder.buildIntent(context, request) } returns intent
        every { foregroundStarter.start(context, intent) } just runs
        every { sessionTokenFactory.build(context) } returns sessionToken
        every { mediaControllerFactory.build(context, sessionToken) } returns future
        every { future.addListener(any(), any()) } just runs
        every { future.isDone } returns false
        every { controllerReleaser.release(future) } just runs
        every { context.stopService(any()) } returns true

        playerServiceHelper.bindAndStartService(context, request) {}
        playerServiceHelper.unbindAndStopService(context)

        verify { controllerReleaser.release(future) }
        verify { context.stopService(any()) }
    }
}
