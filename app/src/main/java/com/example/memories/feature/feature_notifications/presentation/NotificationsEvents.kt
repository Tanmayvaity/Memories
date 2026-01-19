package com.example.memories.feature.feature_notifications.presentation

sealed class NotificationsEvents {
    data class SetAllNotifications(val enabled : Boolean) : NotificationsEvents()
    data class SetReminderNotification(val enabled : Boolean) : NotificationsEvents()
    data class SetOnThisDayNotification(val enabled : Boolean) : NotificationsEvents()

    data class SetReminderTime(val hour : Int, val minute : Int) : NotificationsEvents()
}