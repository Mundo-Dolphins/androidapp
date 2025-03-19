import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import es.mundodolphins.app.services.AudioPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    private var currentPosition: Long = 0L
    private var isServiceBound = false
    private lateinit var audioPlayerService: AudioPlayerService

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> get() = _duration


    val playerState: StateFlow<ExoPlayer?> = _playerState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerBinder
            audioPlayerService = binder.getService()
            _playerState.value = audioPlayerService.getExoPlayer()
            isServiceBound = true

            // Observe player state
            audioPlayerService.playerState.observeForever { state ->
                _isPlaying.postValue(state)
            }

            // Observe player duration
            audioPlayerService.playerDuration.observeForever { duration ->
                _duration.postValue(duration)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    fun initializePlayer(context: Context, mp3Url: String) {
        viewModelScope.launch {
            if (!isServiceBound) {
                val intent = Intent(context, AudioPlayerService::class.java)
                intent.putExtra("MP3_URL", mp3Url)
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                context.startForegroundService(intent)
            }
        }
    }

    fun savePlayerState() {
        _playerState.value?.let {
            currentPosition = it.currentPosition
        }
    }

    fun releasePlayer(context: Context) {
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
        val intent = Intent(context, AudioPlayerService::class.java)
        context.stopService(intent)
    }
}