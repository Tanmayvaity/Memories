package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_notifications.domain.repository.MemoryNotificationScheduler
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository
import kotlin.math.min

class SetReminderNotificationUseCase (
    private val repository: NotificationRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke(enabled : Boolean,hour : Int, minute : Int) {
        repository.enableReminderNotification(enabled)
        if(enabled){
            scheduler.scheduleAlarm(hour, minute)
        }else{
            scheduler.cancelAlarm()
        }

    }
}