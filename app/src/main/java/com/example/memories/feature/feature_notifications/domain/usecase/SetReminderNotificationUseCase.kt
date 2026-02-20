package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryNotificationScheduler

class SetReminderNotificationUseCase (
    private val repository: AppSettingRepository,
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