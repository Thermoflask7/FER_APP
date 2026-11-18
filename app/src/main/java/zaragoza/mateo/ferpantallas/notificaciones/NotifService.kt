package zaragoza.mateo.ferpantallas.notificaciones

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import zaragoza.mateo.ferpantallas.MainActivity
import zaragoza.mateo.ferpantallas.R

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class NotifService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        android.util.Log.d("FCM", "1. Service received data=${message.data} notif=${message.notification?.title}")
        val title = message.data["title"] ?: message.notification?.title ?: "Nueva publicacion"
        val body = message.data["body"] ?: message.notification?.body ?: ""
        showNotification(this, title, body, message.data["postId"])
    }

    companion object {
        const val CHANNEL_ID = "posts"

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, "Publicaciones",
                    NotificationManager.IMPORTANCE_DEFAULT)
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(channel)
            }
        }

        fun showNotification(context: Context, title: String, body: String, postId: String?) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("postId", postId)
            }
            val pending = PendingIntent.getActivity(
                context,
                postId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build()
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}