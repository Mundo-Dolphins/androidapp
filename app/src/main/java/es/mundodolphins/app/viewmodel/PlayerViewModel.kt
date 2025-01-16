package es.mundodolphins.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PlayerViewModel : ViewModel() {
    private val _playerState = MutableStateFlow<ExoPlayer?>(null)

    val playerState: StateFlow<ExoPlayer?> = _playerState

    private var currentPosition: Long = 0L

    @OptIn(UnstableApi::class)
    fun initializePlayer(context: Context, mp3Url: String) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context)
                    .setMediaSourceFactory(
                        DefaultMediaSourceFactory(
                            DefaultHttpDataSource.Factory()
                                .setUserAgent(USER_AGENT)
                                .setAllowCrossProtocolRedirects(true)
                        )
                    )
                    .build()
                    .also {
                        it.setMediaItem(MediaItem.fromUri(Uri.parse(mp3Url)))
                        it.prepare()
                        it.playWhenReady = false
                        it.seekTo(currentPosition)
                        it.addListener(object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                handleError(error)
                            }
                        })
                    }
                _playerState.value = exoPlayer
            }
        }
    }

    fun savePlayerState() {
        _playerState.value?.let {
            currentPosition = it.currentPosition
        }
    }

    fun releasePlayer() {
        _playerState.value?.release()
        _playerState.value = null
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