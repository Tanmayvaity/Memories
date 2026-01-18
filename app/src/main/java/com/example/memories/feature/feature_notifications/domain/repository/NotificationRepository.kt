package com.example.memories.feature.feature_notifications.domain.repository

import kotlinx.coroutines.flow.Flow


interface NotificationRepository{
    val allNotificationAllowed : Flow<Boolean>
    val reminderNotificationAllowed : Flow<Boolean>
    val onThisDayNotificationAllowed : Flow<Boolean>

    suspend fun enableAllNotifications(enabled : Boolean)
    suspend fun enableReminderNotification(enabled : Boolean)
    suspend fun enableOnThisDayNotification(enabled : Boolean)
}