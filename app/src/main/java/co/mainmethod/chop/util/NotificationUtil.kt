package co.mainmethod.chop.util

import android.app.Notification
import android.content.Context
import co.mainmethod.chop.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import timber.log.Timber


/**
 * Created by evan on 12/2/17.
 */
object NotificationUtil {

    const val NOTIF_ID_TASK_SERVICE = 1000

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context) {
        Timber.d("Creating notification channels")
        val id = context.getString(R.string.notification_channel_id_task_service)
        val name = context.getString(R.string.notification_channel_name_task_service)
        val desc = context.getString(R.string.notification_channel_desc_task_service)
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        channel.description = desc
        channel.enableVibration(false)
        channel.setSound(uri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .build())
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun getBuilder(context: Context, notifChannelResId: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, context.getString(notifChannelResId))
    }

    fun notify(context: Context, notificationId: Int, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

}