package es.mundodolphins.app.viewmodel.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import es.mundodolphins.app.services.AudioPlayerService

class PlayerServiceHelper(
    private val intentBuilder: IntentBuilder,
    private val mediaControllerFactory: MediaControllerFactory = DefaultMediaControllerFactory(),
    private val foregroundStarter: ForegroundStarter = DefaultForegroundStarter(),
    private val sessionTokenFactory: SessionTokenFactory = DefaultSessionTokenFactory(),
    private val controllerReleaser: ControllerReleaser = DefaultControllerReleaser(),
) {
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null

    fun bindAndStartService(
        context: Context,
        request: PlaybackRequest,
        onServiceConnected: (MediaController) -> Unit,
    ) {
        val intent = intentBuilder.buildIntent(context, request)
        foregroundStarter.start(context, intent)

        val currentFuture = mediaControllerFuture
        if (currentFuture != null) {
            if (currentFuture.isDone) {
                onServiceConnected(currentFuture.get())
            }
            return
        }

        val sessionToken = sessionTokenFactory.build(context)
        val future = mediaControllerFactory.build(context, sessionToken)
        mediaControllerFuture = future

        future.addListener(
            {
                if (future.isDone) {
                    onServiceConnected(future.get())
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun unbindAndStopService(context: Context) {
        mediaControllerFuture?.let { controllerReleaser.release(it) }
        mediaControllerFuture = null
        val intent = Intent(context, AudioPlayerService::class.java)
        context.stopService(intent)
    }

    data class PlaybackRequest(
        val episodeId: Long,
        val mp3Url: String,
        val currentPosition: Long,
        val title: String,
        val artworkUrl: String?,
    )

    class IntentBuilder {
        @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
        fun buildIntent(
            context: Context,
            request: PlaybackRequest,
        ) =
            Intent(context, AudioPlayerService::class.java).apply {
                putExtra(AudioPlayerService.EXTRA_EPISODE_ID, request.episodeId)
                putExtra(AudioPlayerService.EXTRA_MP3_URL, request.mp3Url)
                putExtra(AudioPlayerService.EXTRA_CURRENT_POSITION, request.currentPosition)
                putExtra(AudioPlayerService.EXTRA_EPISODE_TITLE, request.title)
                putExtra(AudioPlayerService.EXTRA_EPISODE_IMAGE_URL, request.artworkUrl)
            }
    }

    interface MediaControllerFactory {
        fun build(
            context: Context,
            sessionToken: SessionToken,
        ): ListenableFuture<MediaController>
    }

    interface ForegroundStarter {
        fun start(
            context: Context,
            intent: Intent,
        )
    }

    interface SessionTokenFactory {
        fun build(context: Context): SessionToken
    }

    interface ControllerReleaser {
        fun release(future: ListenableFuture<MediaController>)
    }

    private class DefaultMediaControllerFactory : MediaControllerFactory {
        override fun build(
            context: Context,
            sessionToken: SessionToken,
        ): ListenableFuture<MediaController> =
            MediaController.Builder(context, sessionToken).buildAsync()
    }

    private class DefaultForegroundStarter : ForegroundStarter {
        override fun start(
            context: Context,
            intent: Intent,
        ) {
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private class DefaultSessionTokenFactory : SessionTokenFactory {
        override fun build(context: Context): SessionToken =
            SessionToken(context, ComponentName(context, AudioPlayerService::class.java))
    }

    private class DefaultControllerReleaser : ControllerReleaser {
        override fun release(future: ListenableFuture<MediaController>) {
            MediaController.releaseFuture(future)
        }
    }
}
