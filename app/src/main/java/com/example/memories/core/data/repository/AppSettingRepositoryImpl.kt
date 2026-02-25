package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSettingRepositoryImpl @Inject constructor(
    private val otherSettingsDatastore: OtherSettingsDatastore
) : AppSettingRepository {
    override val lockMethod: Flow<String> = otherSettingsDatastore.hiddenMemoriesLockMethod
    override val allNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.allNotificationAllowed
    override val reminderNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.reminderNotificationAllowed
    override val onThisDayNotificationAllowed: Flow<Boolean>
        get() = otherSettingsDatastore.onThisDayNotificationAllowed
    override val reminderTime: Flow<Int>
        get() =  otherSettingsDatastore.reminderTime

    override val hiddenMemoryLockMethod: Flow<String>
        get() = otherSettingsDatastore.hiddenMemoriesLockMethod
    override val hiddenMemoryLockDuration: Flow<String>
        get() = otherSettingsDatastore.hiddenMemoriesLockDuration
    override val isCustomPinSet: Flow<Boolean>
        get() = otherSettingsDatastore.isCustomPinSet()
    override val isDarkModeEnabled: Flow<Boolean>
        get() = otherSettingsDatastore.isDarkModeEnabled

    override suspend fun setDarkMode(toDarkMode: Boolean) {
        otherSettingsDatastore.setDarkMode(toDarkMode)
    }

    override suspend fun enableAllNotifications(enabled: Boolean) {
        otherSettingsDatastore.enableAllNotifications(enabled)
    }

    override suspend fun enableReminderNotification(enabled: Boolean) {
       otherSettingsDatastore.enableReminderNotification(enabled)
    }

    override suspend fun enableOnThisDayNotification(enabled: Boolean) {
        otherSettingsDatastore.enableOnThisDayNotification(enabled)
    }

    override suspend fun setReminderTime(hour: Int, minute: Int) {
        otherSettingsDatastore.setReminderTime(hour, minute)
    }

    override suspend fun setHiddenMemoryLockMethod(method: LockMethod) {
        otherSettingsDatastore.setHiddenMemoriesLockMethod(method)
    }

    override suspend fun setHiddenMemoryLockDuration(duration: LockDuration) {
       otherSettingsDatastore.setHiddenMemoriesLockDuration(duration)
    }

    override suspend fun setHiddenMemoryCustomPin(pin: String) {
        otherSettingsDatastore.setHiddenMemoriesCustomPin(pin)
    }

    override suspend fun isCustomPinCorrect(pin: String) : Boolean {
        return otherSettingsDatastore.isPinCorrect(pin)
    }
}