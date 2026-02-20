package com.example.memories.feature.feature_notifications.domain.usecase


import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryNotificationScheduler

class SetAllNotificationsUseCase (
    private val repository: AppSettingRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke(
        enabled : Boolean,
        hour : Int,
        minute : Int
    ) {
        repository.enableAllNotifications(enabled)
        repository.enableReminderNotification(enabled)
        repository.enableOnThisDayNotification(enabled)
        if(enabled){
            scheduler.scheduleWork()
            scheduler.scheduleAlarm(hour, minute)
        }else{
            scheduler.cancelWork()
            scheduler.cancelAlarm()
        }
    }
}