package com.example.memories.feature.feature_notifications.domain.repository

interface MemoryNotificationScheduler {
    fun scheduleWork()
    fun cancelWork()
}