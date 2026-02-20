package com.example.memories.core.domain.repository

import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {
    val lockMethod: Flow<String>

    val allNotificationAllowed : Flow<Boolean>
    val reminderNotificationAllowed : Flow<Boolean>
    val onThisDayNotificationAllowed : Flow<Boolean>

    val reminderTime : Flow<Int>

    val hiddenMemoryLockMethod: Flow<String>
    val hiddenMemoryLockDuration: Flow<String>

    val isCustomPinSet : Flow<Boolean>

    val isDarkModeEnabled : Flow<Boolean>
    suspend fun setDarkMode( toDarkMode : Boolean)

    suspend fun enableAllNotifications(enabled : Boolean)
    suspend fun enableReminderNotification(enabled : Boolean)
    suspend fun enableOnThisDayNotification(enabled : Boolean)

    suspend fun setReminderTime(hour : Int, minute : Int)

    suspend fun setHiddenMemoryLockMethod(method: LockMethod)
    suspend fun setHiddenMemoryLockDuration(duration: LockDuration)

    suspend fun setHiddenMemoryCustomPin(pin: String)
}