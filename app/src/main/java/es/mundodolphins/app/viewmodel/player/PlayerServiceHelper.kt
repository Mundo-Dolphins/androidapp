package es.mundodolphins.app.viewmodel.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.media3.exoplayer.ExoPlayer
import es.mundodolphins.app.services.AudioPlayerService

class PlayerServiceHelper(
    private val intentBuilder: IntentBuilder,
) {
    private var isServiceBound = false
    private var serviceConnection: ServiceConnection? = null

    fun bindAndStartService(
        context: Context,
        mp3Url: String,
        currentPosition: Long,
        onServiceConnected: (ExoPlayer, AudioPlayerService) -> Unit,
    ) {
        if (!isServiceBound) {
            val intent = intentBuilder.buildIntent(context, mp3Url, currentPosition)

            serviceConnection =
                object : ServiceConnection {
                    override fun onServiceConnected(
                        name: ComponentName?,
                        service: IBinder?,
                    ) {
                        val audioPlayerService =
                            (service as AudioPlayerService.AudioPlayerBinder).getService()
                        onServiceConnected(audioPlayerService.getExoPlayer(), audioPlayerService)
                        isServiceBound = true
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        isServiceBound = false
                    }
                }

            context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
            context.startForegroundService(intent)
        }
    }

    fun unbindAndStopService(context: Context) {
        if (isServiceBound && serviceConnection != null) {
            context.unbindService(serviceConnection!!)
            isServiceBound = false
        }
        val intent = Intent(context, AudioPlayerService::class.java)
        context.stopService(intent)
    }

    class IntentBuilder {
        @androidx.annotation.VisibleForTesting(otherwise = androidx.annotation.VisibleForTesting.PRIVATE)
        fun buildIntent(
            context: Context,
            mp3Url: String,
            currentPosition: Long,
        ) = Intent(context, AudioPlayerService::class.java).apply {
            putExtra("MP3_URL", mp3Url)
            putExtra("CURRENT_POSITION", currentPosition)
        }
    }
}
