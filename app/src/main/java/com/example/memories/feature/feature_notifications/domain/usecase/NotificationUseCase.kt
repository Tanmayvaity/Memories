package com.example.memories.feature.feature_notifications.domain.usecase

data class NotificationUseCase(
    val setAllNotificationsUseCase: SetAllNotificationsUseCase,
    val setReminderNotificationUseCase: SetReminderNotificationUseCase,
    val setOnThisDayNotificationUseCase: SetOnThisDayNotificationUseCase
)
