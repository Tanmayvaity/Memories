package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository

class SetAllNotificationsUseCase (
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(enabled : Boolean) {
        repository.enableAllNotifications(enabled)
    }
}