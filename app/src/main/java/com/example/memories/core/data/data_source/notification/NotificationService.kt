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
import javax.inject.Inject
import com.example.memories.R
import com.example.memories.feature.feature_notifications.data.AlarmManagerService.Companion.REQUEST_CODE_DAILY_REMINDER
import java.time.LocalDate
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

        private const val NOTIFICATION_ID_DAILY_REMINDER = 1001
        private const val NOTIFICATION_ID_ON_THIS_DAY = 1002

        const val ACTION_CREATE_MEMORY = "com.example.memories.ACTION_CREATE_MEMORY"
        const val ACTION_EDIT_MEMORY = "com.example.memories.ACTION_EDIT_MEMORY"
    }

    val isOnThisDayChannelEnabled: Boolean
        get() = notificationManager.getNotificationChannel(ON_THIS_DAY_CHANNEL)
            ?.importance != NotificationManager.IMPORTANCE_NONE

    val isDailyReminderChannelEnabled: Boolean
        get() = notificationManager.getNotificationChannel(DAILY_REMINDER_CHANNEL)
            ?.importance != NotificationManager.IMPORTANCE_NONE

    fun showOnThisDayNotification(bitmap: Bitmap?, title: String, description: String) {
        val pendingIntent = createPendingIntent(REQUEST_CODE_ON_THIS_DAY_NOTIFICATION)
        val builder = createNotificationBuilder(
            ON_THIS_DAY_CHANNEL, bitmap, title, description, pendingIntent
        )
        notificationManager.notify(NOTIFICATION_ID_ON_THIS_DAY, builder.build())
    }

    fun showDailyReminderNotification(title: String, description: String) {
        val pendingIntent = createPendingIntent(REQUEST_CODE_DAILY_REMINDER_NOTIFICATION)
        val actions = listOf(
            NotificationAction(
                icon = R.drawable.ic_create,
                title = "Create",
                pendingIntent = createActionPendingIntent(REQUEST_CODE_CREATE_ACTION, ACTION_CREATE_MEMORY)
            ),
            NotificationAction(
                icon = R.drawable.ic_edit,
                title = "Edit",
                pendingIntent = createActionPendingIntent(REQUEST_CODE_EDIT_ACTION, ACTION_EDIT_MEMORY)
            )
        )
        val builder = createNotificationBuilder(
            DAILY_REMINDER_CHANNEL, null, title, description, pendingIntent, actions
        )
        notificationManager.notify(NOTIFICATION_ID_DAILY_REMINDER, builder.build())
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    private fun createPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createActionPendingIntent(requestCode: Int, action: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.action = action
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
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

    data class NotificationAction(
        val icon: Int,
        val title: String,
        val pendingIntent: PendingIntent
    )
}