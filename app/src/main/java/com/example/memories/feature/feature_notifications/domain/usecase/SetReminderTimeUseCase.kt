package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryNotificationScheduler


class SetReminderTimeUseCase (
    private val repository: AppSettingRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke(hour : Int, minute : Int) {
        repository.setReminderTime(hour,minute)
        scheduler.scheduleAlarm(hour, minute)
    }
}