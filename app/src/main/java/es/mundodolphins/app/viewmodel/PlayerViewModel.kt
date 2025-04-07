package es.mundodolphins.app.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import es.mundodolphins.app.repository.EpisodeRepository
import es.mundodolphins.app.services.AudioPlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(private val episodeRepository: EpisodeRepository) : ViewModel() {

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)

    private var currentPosition: Long = 0L

    private var isServiceBound = false

    private var audioID: Long = 0L

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> get() = _duration

    private val _playerStatus = MutableLiveData<Int>()

    val playerState: StateFlow<ExoPlayer?> = _playerState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val audioPlayerService = (service as AudioPlayerService.AudioPlayerBinder).getService()

            _playerState.value = audioPlayerService.getExoPlayer()
            isServiceBound = true

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

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    fun initializePlayer(context: Context, episodeId: Long, mp3Url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            audioID = episodeId
            currentPosition =
                episodeRepository.getEpisodeById(audioID).first().listenedProgress

            if (!isServiceBound) {
                val intent = Intent(context, AudioPlayerService::class.java)
                intent.putExtra("MP3_URL", mp3Url)
                intent.putExtra("CURRENT_POSITION", currentPosition)
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                context.startForegroundService(intent)
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
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
            savePlayerPosition(currentPosition)
        }
        val intent = Intent(context, AudioPlayerService::class.java)
        context.stopService(intent)
    }

    private fun savePlayerPosition(position: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            episodeRepository.updateEpisodePosition(
                audioID,
                position,
                _playerStatus.value == ExoPlayer.STATE_ENDED
            )
        }
    }
}