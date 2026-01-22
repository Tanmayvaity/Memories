package com.example.memories.core.data.data_source.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.memories.core.util.formatTime
import java.util.Calendar
import javax.inject.Inject

class AlarmManagerService @Inject constructor(
    private val context: Context
) {
    val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    private lateinit var pendingIntent: PendingIntent

    val canScheduleAlarm: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
        }

    companion object {
        const val REMINDER_NOTIFICATION_TITLE = "REMINDER_NOTIFICATION_TITLE"
        const val REMINDER_NOTIFICATION_DESCRIPTION = "REMINDER_NOTIFICATION_DESCRIPTION"

        const val REMINDER_NOTIFICATION_HOUR = "REMINDER_NOTIFICATION_HOUR"
        const val REMINDER_NOTIFICATION_MINUTE = "REMINDER_NOTIFICATION_MINUTE"
        const val REQUEST_CODE_DAILY_REMINDER = 1001
        const val TAG = "AlarmManagerService"
    }

    fun scheduleAlarm(
        hour: Int,
        minute: Int
    ) {

        if(!canScheduleAlarm) {
            Log.d(TAG, "scheduleAlarm: Alarm cannot be scheduled due to lack of permission..., cancelling")
            return
        }

        val triggerTime = calculateAlarmTriggerTime(hour, minute)
        pendingIntent = createPendingIntent(
            receiverClass = AlarmReceiver::class.java,
            requestCode = REQUEST_CODE_DAILY_REMINDER,
            title = "Ready to save today's moment?",
            description = "Don't let today's special memory fade away. It only takes a minute to record"
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
        Log.d(TAG, "scheduleAlarm: Alarm scheduled for ${formatTime(hour, minute)}")

    }

    fun cancelAlarm() {
        if(!::pendingIntent.isInitialized) return

        alarmManager.cancel(pendingIntent)
        Log.i(TAG, "cancelAlarm: Alarm cancelled")


    }

    private fun createPendingIntent(
        receiverClass: Class<*>,
        requestCode: Int,
        title: String,
        description: String,
    ): PendingIntent {
        val intent = Intent(context, receiverClass).apply {
            putExtra(REMINDER_NOTIFICATION_TITLE, title)
            putExtra(REMINDER_NOTIFICATION_DESCRIPTION, description)
            action = AlarmReceiver.ACTION_DAILY_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun calculateAlarmTriggerTime(
        hour: Int,
        minute: Int
    ): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
    }


}