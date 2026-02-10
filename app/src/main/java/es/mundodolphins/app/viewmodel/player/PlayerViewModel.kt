package es.mundodolphins.app.viewmodel.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun initializePlayer(
        context: Context,
        episodeId: Long,
        mp3Url: String,
    ) {
        viewModelScope.launch(ioDispatcher) {
            audioID = episodeId
            currentPosition = episodeRepository.getEpisodeById(audioID).first()?.listenedProgress ?: 0L

            playerServiceHelper.bindAndStartService(context, mp3Url, currentPosition) { exoPlayer, audioPlayerService ->
                _playerState.value = exoPlayer

                audioPlayerService.playerState.observeForever { state ->
                    _isPlaying.postValue(state)
                }

                audioPlayerService.playerDuration.observeForever { duration ->
                    _duration.postValue(duration)
                }

                audioPlayerService.playerStatus.observeForever { status ->
                    _playerStatus.postValue(status)
                }
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
}
