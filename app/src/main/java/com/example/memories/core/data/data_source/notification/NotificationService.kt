package com.example.memories.core.data.data_source.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.memories.MainActivity
import com.example.memories.R
import com.example.memories.core.domain.model.UriType
import com.example.memories.navigation.BASE_URL
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class NotificationService @Inject constructor(
    private val context: Context
) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val ON_THIS_DAY_CHANNEL = "ON_THIS_DAY_CHANNEL"
        const val DAILY_REMINDER_CHANNEL = "DAILY_REMINDER_CHANNEL"

        private const val REQUEST_CODE_DAILY_REMINDER_NOTIFICATION = 2001
        private const val REQUEST_CODE_ON_THIS_DAY_NOTIFICATION = 2002
        private const val REQUEST_CODE_CREATE_ACTION = 3001
        private const val REQUEST_CODE_EDIT_ACTION = 3002

        const val NOTIFICATION_ID_DAILY_REMINDER = 1001
        const val NOTIFICATION_ID_ON_THIS_DAY = 1002
    }

    val isOnThisDayChannelEnabled: Boolean
        get() = notificationManager.getNotificationChannel(ON_THIS_DAY_CHANNEL)
            ?.importance != NotificationManager.IMPORTANCE_NONE

    val isDailyReminderChannelEnabled: Boolean
        get() = notificationManager.getNotificationChannel(DAILY_REMINDER_CHANNEL)
            ?.importance != NotificationManager.IMPORTANCE_NONE

    fun showOnThisDayNotification(bitmap: Bitmap?, title: String, description: String) {
        val pendingIntent = createPendingIntent(
            requestCode = REQUEST_CODE_ON_THIS_DAY_NOTIFICATION,
            uri = "$BASE_URL/search"
        )
        val builder = createNotificationBuilder(
            channelId = ON_THIS_DAY_CHANNEL,
            bitmap = bitmap,
            title = title,
            description = description,
            pendingIntent = pendingIntent
        )
        notificationManager.notify(NOTIFICATION_ID_ON_THIS_DAY, builder.build())
    }

    fun showDailyReminderNotification(title: String, description: String) {
        val pendingIntent = createPendingIntent(
            requestCode = REQUEST_CODE_DAILY_REMINDER_NOTIFICATION
        )
        val actions = listOf(
            NotificationAction(
                icon = R.drawable.ic_create,
                title = "Create",
                pendingIntent = createPendingIntent(
                    requestCode = REQUEST_CODE_CREATE_ACTION,
                    uri = "$BASE_URL/create",
                    oneShot = true
                )
            ),
            NotificationAction(
                icon = R.drawable.ic_edit,
                title = "Edit",
                pendingIntent = createPendingIntent(
                    requestCode = REQUEST_CODE_EDIT_ACTION,
                    uri = "$BASE_URL/edit",
                    oneShot = true
                )
            ),

        )
        val builder = createNotificationBuilder(
            channelId = DAILY_REMINDER_CHANNEL,
            bitmap = null,
            title = title,
            description = description,
            pendingIntent = pendingIntent,
            actions = actions
        )
        notificationManager.notify(NOTIFICATION_ID_DAILY_REMINDER, builder.build())
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    private fun createPendingIntent(
        requestCode: Int,
        uri: String? = null,
        oneShot: Boolean = false
    ): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            uri?.let { data = it.toUri() }
            setClass(context, MainActivity::class.java)
        }

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(requestCode, flags)!!
        }
    }

    private fun createNotificationBuilder(
        channelId: String,
        bitmap: Bitmap?,
        title: String,
        description: String,
        pendingIntent: PendingIntent,
        actions: List<NotificationAction> = emptyList()
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_camera)
            .setContentTitle(title)
            .setContentText(description)
            .setLargeIcon(bitmap)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                actions.forEach { addAction(it.icon, it.title, it.pendingIntent) }
            }
    }

    private data class NotificationAction(
        val icon: Int,
        val title: String,
        val pendingIntent: PendingIntent
    )
}