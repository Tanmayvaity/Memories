package com.example.memories.feature.feature_notifications.data

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val otherSettingsDatastore: OtherSettingsDatastore
) : NotificationRepository {
    override val allNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.allNotificationAllowed

    override val reminderNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.reminderNotificationAllowed

    override val onThisDayNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.onThisDayNotificationAllowed

    override suspend fun enableAllNotifications(enabled: Boolean) {
        otherSettingsDatastore.enableAllNotifications(enabled)
    }

    override suspend fun enableReminderNotification(enabled: Boolean) {
        otherSettingsDatastore.enableReminderNotification(enabled)
    }

    override suspend fun enableOnThisDayNotification(enabled: Boolean) {
        otherSettingsDatastore.enableOnThisDayNotification(enabled)
    }

}