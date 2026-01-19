package com.example.memories.feature.feature_notifications.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.feature.feature_notifications.data.AlarmManagerService.Companion.REMINDER_NOTIFICATION_DESCRIPTION
import com.example.memories.feature.feature_notifications.data.AlarmManagerService.Companion.REMINDER_NOTIFICATION_HOUR
import com.example.memories.feature.feature_notifications.data.AlarmManagerService.Companion.REMINDER_NOTIFICATION_MINUTE
import com.example.memories.feature.feature_notifications.data.AlarmManagerService.Companion.REMINDER_NOTIFICATION_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationService : NotificationService
    @Inject lateinit var alarmManagerService: AlarmManagerService
    @Inject lateinit var otherSettingsDatastore: OtherSettingsDatastore

    companion object {
        const val ACTION_DAILY_REMINDER = "com.example.memories.ACTION_DAILY_REMINDER"
        const val TAG = "AlarmReceiver"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when(intent?.action){
            ACTION_DAILY_REMINDER -> {
                if(!notificationService.isDailyReminderChannelEnabled){
                    return
                }
                val title = intent.getStringExtra(REMINDER_NOTIFICATION_TITLE)
                val description = intent.getStringExtra(REMINDER_NOTIFICATION_DESCRIPTION)
                val hour = intent.getIntExtra(REMINDER_NOTIFICATION_HOUR,22)
                val minute = intent.getIntExtra(REMINDER_NOTIFICATION_MINUTE,0)

                if(title != null && description!= null){
                    // show notification
                    notificationService.showDailyReminderNotification(
                        title,
                        description
                    )
                }
                if(alarmManagerService.canScheduleAlarm){
                    alarmManagerService.scheduleAlarm(hour,minute)
                }


            }
            ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "onReceive: BOOT_COMPLETED")
                val pendingResult = goAsync()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val reminderTime = otherSettingsDatastore.reminderTime.first()
                        val hour = reminderTime / 60
                        val minute = reminderTime % 60

                        Log.i(TAG, "onReceive: ${hour}h ${minute}m")
                        if (alarmManagerService.canScheduleAlarm && notificationService.isDailyReminderChannelEnabled ) {
                            alarmManagerService.scheduleAlarm(hour, minute)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

        }
    }

}