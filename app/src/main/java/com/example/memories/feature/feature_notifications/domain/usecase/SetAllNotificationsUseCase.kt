package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_notifications.domain.repository.MemoryNotificationScheduler
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository

class SetAllNotificationsUseCase (
    private val repository: NotificationRepository,
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