package es.mundodolphins.app.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.mundodolphins.app.MainActivity
import es.mundodolphins.app.R
import es.mundodolphins.app.notifications.PushNotificationData

class MundoDolphinsMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title =
            message.data[DATA_TITLE]
                ?: message.notification?.title
                ?: getString(R.string.app_name)
        val body =
            message.data[DATA_BODY]
                ?: message.notification?.body
                ?: getString(R.string.notification_new_content_default_body)
        val target = PushNotificationData.parseTarget(message.data)

        showNotification(
            title = title,
            body = body,
            target = target,
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        target: PushNotificationData.Target?,
    ) {
        createChannelIfNeeded()

        if (!canPostNotifications()) return

        val notificationIntent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                PushNotificationData.applyTarget(this, target)
            }

        val notificationId = target?.hashCode() ?: title.hashCode()
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_mundodolphins)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.mundo_dolphins_small))
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_news_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = getString(R.string.notification_channel_news_description)
            }
        manager.createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val DATA_TITLE = "title"
        private const val DATA_BODY = "body"

        private const val CHANNEL_ID = "new_content_channel"
    }
}
