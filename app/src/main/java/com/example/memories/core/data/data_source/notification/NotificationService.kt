package com.example.memories.core.data.data_source.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.memories.MainActivity
import com.example.memories.core.domain.repository.NotificationService
import javax.inject.Inject
import com.example.memories.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
class NotificationServiceImpl @Inject constructor(
    private val context: Context
) : NotificationService {

    val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        const val ON_THIS_DAY_CHANNEL = "ON_THIS_DAY_CHANNEL"
    }

    override val isOnThisDayChannelEnabled: Boolean

        get() = notificationManager.getNotificationChannel(ON_THIS_DAY_CHANNEL).importance == NotificationManager.IMPORTANCE_NONE

    @RequiresApi(Build.VERSION_CODES.O)
    override fun showOnThisDayNotification(
        bitmap : Bitmap?,
        title : String,
        description : String
    ) {
        val builder = createNotificationBuilder(
            ON_THIS_DAY_CHANNEL,bitmap,title,description
        )
        val notificationId = LocalDate.now().toEpochDay().toInt()
        notificationManager.notify(
            notificationId, builder.build()
        )

    }

    override fun cancelOnThisDayNotification(id: Int) {
        notificationManager.cancel(id)
    }

    private fun createNotificationBuilder(
        channelId: String,
        bitmap : Bitmap?,
        title : String,
        description : String
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_camera)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }
}