package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.feature.feature_notifications.domain.repository.MemoryNotificationScheduler
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository


class SetReminderTimeUseCase (
    private val repository: NotificationRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke(hour : Int, minute : Int) {
        repository.setReminderTime(hour,minute)
        scheduler.scheduleAlarm(hour, minute)
    }
}