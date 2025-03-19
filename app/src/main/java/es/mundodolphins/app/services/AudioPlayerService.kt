package es.mundodolphins.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.C.WAKE_MODE_NETWORK
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import es.mundodolphins.app.R

class AudioPlayerService : Service() {
    private val binder = AudioPlayerBinder()
    private lateinit var exoPlayer: ExoPlayer

    val playerState = MutableLiveData<Boolean>()
    val playerDuration = MutableLiveData<Long>()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    DefaultHttpDataSource.Factory()
                        .setUserAgent(USER_AGENT)
                        .setAllowCrossProtocolRedirects(true)
                )
            )
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerState.postValue(isPlaying)
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    playerDuration.postValue(exoPlayer.duration)
                }
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mp3Url = intent?.getStringExtra("MP3_URL") ?: return START_NOT_STICKY
        exoPlayer.setMediaItem(MediaItem.fromUri(mp3Url))
        exoPlayer.prepare()
        exoPlayer.setWakeMode(WAKE_MODE_NETWORK)
        exoPlayer.playWhenReady = true

//        it.seekTo(currentPosition)
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                handleError(error)
            }
        })

        val notification = createNotification()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun onBind(intent: Intent?) = binder

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    fun getExoPlayer() = exoPlayer

    private fun createNotification(): Notification {
        val channelId = "audio_playback"
        val channelName = "Audio Playback"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mundo Dolphins")
            .setContentText("Episodio reproduciendo")
            .setSmallIcon(R.drawable.ic_play)
            .build()
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

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0"
    }
}