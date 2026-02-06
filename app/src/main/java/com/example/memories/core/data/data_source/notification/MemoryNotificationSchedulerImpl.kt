package com.example.memories.core.data.data_source.notification

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.memories.core.domain.repository.MemoryNotificationScheduler
import com.example.memories.core.data.data_source.alarm.AlarmManagerService
import com.example.memories.core.data.data_source.worker.OnThisDayNotificationWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

        // 1. Set target to exactly 7:00:00 AM
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 2. Adjust to tomorrow if 7 AM has passed
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = target.timeInMillis - System.currentTimeMillis()

        // --- READABLE LOGGING ---
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val readableTarget = sdf.format(target.time)

        val hours = TimeUnit.MILLISECONDS.toHours(delay)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(delay) % 60

        Log.i(TAG, "ScheduleWork: Intentional trigger time: $readableTarget")
        Log.i(TAG, "ScheduleWork: Initial delay is $hours h $minutes m")
        // ------------------------

        val workRequest = PeriodicWorkRequestBuilder<OnThisDayNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(OnThisDayNotificationWorker.ON_THIS_DAY_NOTIFICATION_WORKER)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        // 4. Using KEEP ensures we don't restart the timer if it's already scheduled
        workManager.enqueueUniquePeriodicWork(
            OnThisDayNotificationWorker.ON_THIS_DAY_NOTIFICATION_WORKER,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.d(TAG, "Work: Enqueued with KEEP policy.")
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