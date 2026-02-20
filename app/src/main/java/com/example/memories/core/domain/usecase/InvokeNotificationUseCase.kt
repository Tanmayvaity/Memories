package com.example.memories.core.domain.usecase

import android.util.Log
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryNotificationScheduler
import kotlinx.coroutines.flow.first

class InvokeNotificationUseCase (
    private val repository: AppSettingRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke() {
        // Handle reminder alarm
        val shouldScheduleReminder = scheduler.isReminderChannelAllowed
                && repository.reminderNotificationAllowed.first() && scheduler.canAlarmSchedule

        if (shouldScheduleReminder) {
            val reminderTime = repository.reminderTime.first()
            scheduler.scheduleAlarm(reminderTime / 60, reminderTime % 60)
        } else {
            scheduler.cancelAlarm()
        }

        // Handle on-this-day work
        val shouldScheduleOnThisDay = scheduler.isOnThisDayChannelAllowed
                && repository.onThisDayNotificationAllowed.first()

        if (shouldScheduleOnThisDay) {
            scheduler.scheduleWork()
        } else {
            scheduler.cancelWork()
        }

        Log.d("InvokeNotificationUseCase", "invoke: reminder=$shouldScheduleReminder, onThisDay=$shouldScheduleOnThisDay")
    }
}


