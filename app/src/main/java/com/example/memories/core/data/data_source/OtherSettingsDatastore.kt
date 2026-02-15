package com.example.memories.core.data.data_source

import android.R
import android.R.attr.enabled
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import java.lang.Compiler.enable
import java.lang.Exception

class OtherSettingsDatastore(
    val context: Context
) {
    companion object {
        private const val TAG = "OtherSettingsDatastore"
        private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "other_settings")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val ALL_NOTIFICATIONS_ALLOWED = booleanPreferencesKey("all_notifications_allowed")
        val REMINDER_NOTIFICATION_ALLOWED = booleanPreferencesKey("reminder_notification_allowed")
        val ON_THIS_DAY_NOTIFICATION_ALLOWED =
            booleanPreferencesKey("on_this_day_notification_allowed")

        val REMINDER_TIME = intPreferencesKey("reminder_time")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        val HIDDEN_MEMORIES_LOCK_METHOD = stringPreferencesKey("HIDDEN_MEMORIES_LOCK_METHOD")
        val HIDDEN_MEMORIES_LOCK_DURATION = stringPreferencesKey("HIDDEN_MEMORIES_LOCK_DURATION")

        val HIDDEN_MEMORIES_CUSTOM_PIN = stringPreferencesKey("HIDDEN_MEMORIES_CUSTOM_PIN")
    }

    val isDarkModeEnabled = context.datastore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val allNotificationAllowed = context.datastore.data.map { preferences ->
        preferences[ALL_NOTIFICATIONS_ALLOWED] ?: true
    }

    val reminderNotificationAllowed = context.datastore.data.map { preferences ->
        preferences[REMINDER_NOTIFICATION_ALLOWED] ?: true
    }

    val onThisDayNotificationAllowed = context.datastore.data.map { preferences ->
        preferences[ON_THIS_DAY_NOTIFICATION_ALLOWED] ?: true
    }

    val reminderTime = context.datastore.data.map { preferences ->
        preferences[REMINDER_TIME] ?: (22 * 60 + 0)
    }

    val isOnboardingCompleted = context.datastore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    private val hiddenMemoriesCustomPin = context.datastore.data.map { preferences ->
        preferences[HIDDEN_MEMORIES_CUSTOM_PIN] ?: ""
    }


    val hiddenMemoriesLockMethod: Flow<String> = context.datastore.data.map { preferences ->
        val saved = preferences[HIDDEN_MEMORIES_LOCK_METHOD] ?: LockMethod.NONE.name
        try {
            LockMethod.valueOf(saved)
            saved
        } catch (e: Exception) {
            LockMethod.NONE.name
        }
    }

    val hiddenMemoriesLockDuration = context.datastore.data.map { preferences ->
        preferences[HIDDEN_MEMORIES_LOCK_DURATION] ?: LockDuration.ONE_MINUTE.name
    }

    fun isCustomPinSet(): Flow<Boolean> {
        return hiddenMemoriesCustomPin.map { it.isNotEmpty() }
    }

    suspend fun setHiddenMemoriesCustomPin(pin: String) {
        context.datastore.edit { preferences ->
            preferences[HIDDEN_MEMORIES_CUSTOM_PIN] = pin
        }
    }

    suspend fun setHiddenMemoriesLockMethod(method: LockMethod) {
        context.datastore.edit { preferences ->
            preferences[HIDDEN_MEMORIES_LOCK_METHOD] = method.name
        }
    }

    suspend fun setHiddenMemoriesLockDuration(duration: LockDuration) {
        context.datastore.edit { preferences ->
            preferences[HIDDEN_MEMORIES_LOCK_DURATION] = duration.name
        }
    }

    suspend fun setDarkMode(toDarkMode: Boolean) {
        context.datastore.edit { preferences ->
//            val enable = preferences[DARK_MODE_KEY] ?: false
            preferences[DARK_MODE_KEY] = toDarkMode
//            Log.d(TAG, "setDarkMode: ${!enable} ")
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.datastore.edit { preferences ->
            preferences[REMINDER_TIME] = hour * 60 + minute
        }
    }

    suspend fun enableAllNotifications(enabled: Boolean) {
        Log.d(TAG, "enableAllNotifications: ${enabled}")
        context.datastore.edit { preferences ->
            preferences[ALL_NOTIFICATIONS_ALLOWED] = enabled
        }
    }

    suspend fun enableReminderNotification(enabled: Boolean) {
        Log.d(TAG, "enableAllNotifications: ${enabled}")
        context.datastore.edit { preferences ->
            preferences[REMINDER_NOTIFICATION_ALLOWED] = enabled
        }
    }

    suspend fun enableOnThisDayNotification(enabled: Boolean) {
        Log.d(TAG, "enableAllNotifications: ${enabled}")
        context.datastore.edit { preferences ->
            preferences[ON_THIS_DAY_NOTIFICATION_ALLOWED] = enabled
        }
    }

    suspend fun setOnboardingCompleted() {
        context.datastore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }

}