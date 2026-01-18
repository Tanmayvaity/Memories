package com.example.memories.feature.feature_notifications.data

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_notifications.data.OnThisDayNotificationWorker.Companion.ON_THIS_DAY_NOTIFICATION_WORKER
import com.example.memories.feature.feature_notifications.domain.repository.MemoryNotificationScheduler
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MemoryNotificationSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : MemoryNotificationScheduler {

    companion object {

        private val TAG = "MemoryNotificationSchedulerImpl"
    }


    override fun scheduleWork() {

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
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
                .addTag(ON_THIS_DAY_NOTIFICATION_WORKER)
                .build()

        workManager.enqueueUniquePeriodicWork(
            ON_THIS_DAY_NOTIFICATION_WORKER,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

    }

    override fun cancelWork() {
        Log.d(TAG, "cancelWork: cancelling work ${ON_THIS_DAY_NOTIFICATION_WORKER}")
        workManager.cancelAllWorkByTag(ON_THIS_DAY_NOTIFICATION_WORKER)
    }

}