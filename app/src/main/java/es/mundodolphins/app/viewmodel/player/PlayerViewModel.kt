package es.mundodolphins.app.viewmodel.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import es.mundodolphins.app.repository.EpisodeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val episodeRepository: EpisodeRepository,
    private val playerServiceHelper: PlayerServiceHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    private val _playerState = MutableStateFlow<Player?>(null)
    val playerState: StateFlow<Player?> = _playerState

    private var currentPosition: Long = 0L
    private var audioID: Long = 0L

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> get() = _duration

    private val _playerStatus = MutableLiveData<Int>()
    val playerStatus: LiveData<Int> get() = _playerStatus

    private val playerListener =
        object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.postValue(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _playerStatus.postValue(playbackState)
                _duration.postValue(sanitizeDuration(_playerState.value?.duration))
            }
        }

    fun initializePlayer(
        context: Context,
        episodeId: Long,
        mp3Url: String,
        title: String,
        artworkUrl: String?,
    ) {
        viewModelScope.launch(ioDispatcher) {
            audioID = episodeId
            currentPosition = episodeRepository.getEpisodeById(audioID).first()?.listenedProgress ?: 0L
            val request =
                PlayerServiceHelper.PlaybackRequest(
                    episodeId = episodeId,
                    mp3Url = mp3Url,
                    currentPosition = currentPosition,
                    title = title,
                    artworkUrl = artworkUrl,
                )

            playerServiceHelper.bindAndStartService(context, request) { mediaController ->
                _playerState.value?.removeListener(playerListener)
                _playerState.value = mediaController
                mediaController.addListener(playerListener)
                _isPlaying.postValue(mediaController.isPlaying)
                _duration.postValue(sanitizeDuration(mediaController.duration))
                _playerStatus.postValue(mediaController.playbackState)
            }
        }
    }

    fun savePlayerState() {
        _playerState.value?.let {
            currentPosition = it.currentPosition
            savePlayerPosition(currentPosition)
        }
    }

    fun releasePlayer(context: Context) {
        _playerState.value?.removeListener(playerListener)
        playerServiceHelper.unbindAndStopService(context)
        savePlayerPosition(currentPosition)
    }

    private fun savePlayerPosition(position: Long) {
        viewModelScope.launch(ioDispatcher) {
            episodeRepository.updateEpisodePosition(
                audioID,
                position,
                _playerStatus.value == Player.STATE_ENDED,
            )
        }
    }

    private fun sanitizeDuration(durationMs: Long?): Long {
        val duration = durationMs ?: 0L
        return if (duration <= 0L || duration == C.TIME_UNSET) 0L else duration
    }
}
