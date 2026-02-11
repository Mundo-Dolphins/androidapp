package es.mundodolphins.app.services

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.OptIn
import androidx.annotation.VisibleForTesting
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.WAKE_MODE_NETWORK
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList
import es.mundodolphins.app.MainActivity
import es.mundodolphins.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("TooManyFunctions")
class AudioPlayerService : MediaSessionService() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val artworkCache = mutableMapOf<Long, ByteArray>()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer = exoPlayerFactory(this)
        exoPlayer.setAudioAttributes(
            AudioAttributes
                .Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true,
        )
        exoPlayer.setHandleAudioBecomingNoisy(true)
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    handleError(error)
                }
            },
        )

        mediaSession =
            MediaSession
                .Builder(this, exoPlayer)
                .setSessionActivity(buildSessionActivityIntent(null))
                .setCallback(SessionCallback())
                .setMediaButtonPreferences(defaultCommandButtons())
                .build()
    }

    override fun onGetSession(controllerInfo: ControllerInfo): MediaSession = mediaSession

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val mp3Url = intent?.getStringExtra(EXTRA_MP3_URL) ?: return START_STICKY
        val episodeId = intent.getLongExtra(EXTRA_EPISODE_ID, 0L)
        val currentPosition = intent.getLongExtra(EXTRA_CURRENT_POSITION, 0L)
        val mediaItem = buildMediaItem(intent)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.setWakeMode(WAKE_MODE_NETWORK)
        exoPlayer.seekTo(currentPosition)
        exoPlayer.playWhenReady = true

        mediaSession.setSessionActivity(buildSessionActivityIntent(episodeId))
        loadAndCacheArtwork(episodeId, intent.getStringExtra(EXTRA_EPISODE_IMAGE_URL))

        return START_STICKY
    }

    override fun onDestroy() {
        mediaSession.release()
        exoPlayer.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getExoPlayer(): Player = exoPlayer

    private fun buildMediaItem(intent: Intent): MediaItem {
        val mp3Url = intent.getStringExtra(EXTRA_MP3_URL).orEmpty()
        val title = intent.getStringExtra(EXTRA_EPISODE_TITLE).orEmpty()
        val artworkUrl = intent.getStringExtra(EXTRA_EPISODE_IMAGE_URL)
        val mediaMetadata =
            MediaMetadata
                .Builder()
                .setTitle(if (title.isBlank()) getString(R.string.app_name) else title)
                .setArtist(getString(R.string.app_name))
                .apply {
                    if (!artworkUrl.isNullOrBlank()) {
                        setArtworkUri(Uri.parse(artworkUrl))
                    }
                }.build()

        return MediaItem
            .Builder()
            .setUri(mp3Url)
            .setMediaMetadata(mediaMetadata)
            .build()
    }

    private fun loadAndCacheArtwork(
        episodeId: Long,
        artworkUrl: String?,
    ) {
        if (episodeId <= 0L || artworkUrl.isNullOrBlank()) return
        val cached = artworkCache[episodeId]
        if (cached != null) {
            updateCurrentItemArtwork(cached)
            return
        }

        serviceScope.launch {
            val artworkData = withContext(Dispatchers.IO) { downloadArtwork(artworkUrl) } ?: return@launch
            artworkCache[episodeId] = artworkData
            updateCurrentItemArtwork(artworkData)
        }
    }

    private fun updateCurrentItemArtwork(artworkData: ByteArray) {
        val currentIndex = exoPlayer.currentMediaItemIndex
        val currentItem = exoPlayer.currentMediaItem ?: return
        val updatedMetadata =
            currentItem.mediaMetadata
                .buildUpon()
                .setArtworkData(artworkData, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                .build()
        exoPlayer.replaceMediaItem(
            currentIndex,
            currentItem
                .buildUpon()
                .setMediaMetadata(updatedMetadata)
                .build(),
        )
    }

    @Suppress("ReturnCount")
    private fun downloadArtwork(url: String): ByteArray? {
        val connection = (URL(url).openConnection() as? HttpURLConnection) ?: return null
        return try {
            connection.instanceFollowRedirects = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.inputStream.use { input ->
                val bitmap = BitmapFactory.decodeStream(input) ?: return null
                ByteArrayOutputStream().use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                    stream.toByteArray()
                }
            }
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun defaultCommandButtons(): ImmutableList<CommandButton> =
        ImmutableList.of(
            CommandButton
                .Builder(CommandButton.ICON_SKIP_BACK_15)
                .setDisplayName(getString(R.string.seek_back_15))
                .setPlayerCommand(Player.COMMAND_SEEK_BACK)
                .setSlots(CommandButton.SLOT_BACK)
                .build(),
            CommandButton
                .Builder(CommandButton.ICON_PLAY)
                .setDisplayName(getString(R.string.play_pause))
                .setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                .setSlots(CommandButton.SLOT_CENTRAL)
                .build(),
            CommandButton
                .Builder(CommandButton.ICON_SKIP_FORWARD_30)
                .setDisplayName(getString(R.string.seek_forward_30))
                .setPlayerCommand(Player.COMMAND_SEEK_FORWARD)
                .setSlots(CommandButton.SLOT_FORWARD)
                .build(),
            CommandButton
                .Builder(CommandButton.ICON_STOP)
                .setDisplayName(getString(R.string.stop))
                .setPlayerCommand(Player.COMMAND_STOP)
                .setSlots(CommandButton.SLOT_OVERFLOW)
                .build(),
        )

    private fun buildSessionActivityIntent(episodeId: Long?): PendingIntent {
        val intent =
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_EPISODE_ID, episodeId ?: -1L)

        return PendingIntent.getActivity(
            this,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun handleError(error: PlaybackException) {
        when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                println("Network connection error")
            }

            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> {
                println("File not found")
            }

            PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                println("Decoder initialization error")
            }

            else -> {
                println("Other error: ${error.message}")
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun simulateExoPlayerError() {
        throw RuntimeException("Simulated ExoPlayer error")
    }

    private inner class SessionCallback : MediaSession.Callback

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0"
        private const val NOTIFICATION_REQUEST_CODE = 1001

        const val EXTRA_MP3_URL = "MP3_URL"
        const val EXTRA_CURRENT_POSITION = "CURRENT_POSITION"
        const val EXTRA_EPISODE_ID = "EPISODE_ID"
        const val EXTRA_EPISODE_TITLE = "EPISODE_TITLE"
        const val EXTRA_EPISODE_IMAGE_URL = "EPISODE_IMAGE_URL"

        @OptIn(UnstableApi::class)
        private val defaultExoPlayerFactory: (AudioPlayerService) -> ExoPlayer =
            { service ->
                ExoPlayer
                    .Builder(service)
                    .setSeekBackIncrementMs(15_000L)
                    .setSeekForwardIncrementMs(30_000L)
                    .setMediaSourceFactory(
                        DefaultMediaSourceFactory(
                            DefaultHttpDataSource
                                .Factory()
                                .setUserAgent(USER_AGENT)
                                .setAllowCrossProtocolRedirects(true),
                        ),
                    ).build()
            }

        @VisibleForTesting
        var exoPlayerFactory: (AudioPlayerService) -> ExoPlayer = defaultExoPlayerFactory

        @VisibleForTesting
        internal fun resetExoPlayerFactory() {
            exoPlayerFactory = defaultExoPlayerFactory
        }
    }
}
