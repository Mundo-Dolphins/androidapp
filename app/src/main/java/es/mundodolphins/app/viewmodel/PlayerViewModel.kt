import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import es.mundodolphins.app.services.AudioPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("PlayerPreferences", MODE_PRIVATE)

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)

    private var currentPosition: Long = 0L

    private var isServiceBound = false

    private lateinit var audioID: String

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

            audioPlayerService.getExoPlayer().seekTo(currentPosition)

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
            audioID = mp3Url.hashString()
            currentPosition = getPlayerPosition(audioID)

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
            savePlayerPosition(audioID, currentPosition)
        }
        val intent = Intent(context, AudioPlayerService::class.java)
        context.stopService(intent)
    }

    private fun savePlayerPosition(audioId: String, position: Long) {
        sharedPreferences.edit {
            putLong(audioId, position)
        }
    }

    fun getPlayerPosition(audioId: String): Long {
        return sharedPreferences.getLong(audioId, 0L)
    }

    private fun String.hashString(): String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(this.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    companion object {
        private const val HEX_CHARS = "0123456789ABCDEF"
    }
}