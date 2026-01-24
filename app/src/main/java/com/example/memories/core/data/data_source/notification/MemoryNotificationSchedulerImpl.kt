package com.example.memories.core.data.data_source.notification

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.memories.core.domain.repository.MemoryNotificationScheduler
import com.example.memories.core.data.data_source.alarm.AlarmManagerService
import com.example.memories.core.data.data_source.worker.OnThisDayNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MemoryNotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val alarmManagerService: AlarmManagerService,
    private val notificationService : NotificationService
) : MemoryNotificationScheduler {

    companion object {

        private val TAG = "MemoryNotificationSchedulerImpl"
    }

    override val isOnThisDayChannelAllowed: Boolean
        get() = notificationService.isOnThisDayChannelEnabled

    override val isReminderChannelAllowed: Boolean
        get() = notificationService.isDailyReminderChannelEnabled

    override val canAlarmSchedule: Boolean
        get() = alarmManagerService.canScheduleAlarm


    override fun scheduleWork() {

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,7)
            set(Calendar.MINUTE,0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val diff = target.timeInMillis - System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        Log.i(
            TAG,
            "ScheduleWork: Scheduling reminder notification for $hours h $minutes m"
        )

        val workRequest =
            PeriodicWorkRequestBuilder<OnThisDayNotificationWorker>(
                repeatInterval = 24,
                flexTimeInterval = 2,
                flexTimeIntervalUnit = TimeUnit.HOURS,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
            )
                .setInitialDelay(
                    target.timeInMillis - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .addTag(OnThisDayNotificationWorker.Companion.ON_THIS_DAY_NOTIFICATION_WORKER)
                .build()

        workManager.enqueueUniquePeriodicWork(
            OnThisDayNotificationWorker.Companion.ON_THIS_DAY_NOTIFICATION_WORKER,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        Log.d(TAG, "Work : work  scheduled")

    }

    override fun cancelWork() {
        Log.d(TAG, "Work: cancelling work ${OnThisDayNotificationWorker.Companion.ON_THIS_DAY_NOTIFICATION_WORKER}")
        workManager.cancelAllWorkByTag(OnThisDayNotificationWorker.Companion.ON_THIS_DAY_NOTIFICATION_WORKER)
    }

    override fun scheduleAlarm(hour: Int, minute: Int) {
        Log.d(TAG, "alarm: Alarm may be scheduled")
        alarmManagerService.scheduleAlarm(hour,minute)
    }

    override fun cancelAlarm() {
        Log.d(TAG, "alarm: alarm  cancelled")
        alarmManagerService.cancelAlarm()
    }

}