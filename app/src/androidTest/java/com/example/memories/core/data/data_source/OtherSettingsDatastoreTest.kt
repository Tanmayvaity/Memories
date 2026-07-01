package com.example.memories.core.data.data_source

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OtherSettingsDatastoreTest {

    private lateinit var datastore: OtherSettingsDatastore

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        datastore = OtherSettingsDatastore(context)

        // reset to defaults since the DataStore file persists across test runs on device
        runBlocking {
            datastore.setDarkMode(false)
            datastore.enableAllNotifications(true)
            datastore.enableReminderNotification(true)
            datastore.enableOnThisDayNotification(true)
            datastore.setReminderTime(22, 0)
            datastore.setHiddenMemoriesLockMethod(LockMethod.NONE)
            datastore.setHiddenMemoriesLockDuration(LockDuration.ONE_MINUTE)
            datastore.setHiddenMemoriesCustomPin("")
        }
    }

    @Test
    fun darkMode_defaultsToFalse() = runTest {
        assertFalse(datastore.isDarkModeEnabled.first())
    }

    @Test
    fun setDarkMode_persistsValue() = runTest {
        datastore.setDarkMode(true)
        assertTrue(datastore.isDarkModeEnabled.first())
    }

    @Test
    fun reminderTime_defaultsTo22_00() = runTest {
        assertEquals(22 * 60, datastore.reminderTime.first())
    }

    @Test
    fun setReminderTime_encodesHourAndMinute() = runTest {
        datastore.setReminderTime(7, 30)
        assertEquals(7 * 60 + 30, datastore.reminderTime.first())
    }

    @Test
    fun notificationFlags_defaultToTrue() = runTest {
        assertTrue(datastore.allNotificationAllowed.first())
        assertTrue(datastore.reminderNotificationAllowed.first())
        assertTrue(datastore.onThisDayNotificationAllowed.first())
    }

    @Test
    fun enableAllNotifications_persistsFalse() = runTest {
        datastore.enableAllNotifications(false)
        assertFalse(datastore.allNotificationAllowed.first())
    }

    @Test
    fun enableReminderNotification_persistsFalse() = runTest {
        datastore.enableReminderNotification(false)
        assertFalse(datastore.reminderNotificationAllowed.first())
    }

    @Test
    fun enableOnThisDayNotification_persistsFalse() = runTest {
        datastore.enableOnThisDayNotification(false)
        assertFalse(datastore.onThisDayNotificationAllowed.first())
    }

    @Test
    fun onboardingCompleted_defaultsToFalseThenPersistsTrue() = runTest {
        assertFalse(datastore.isOnboardingCompleted.first())
        datastore.setOnboardingCompleted()
        assertTrue(datastore.isOnboardingCompleted.first())
    }

    @Test
    fun lockMethod_defaultsToNone() = runTest {
        assertEquals(LockMethod.NONE.name, datastore.hiddenMemoriesLockMethod.first())
    }

    @Test
    fun setHiddenMemoriesLockMethod_persistsValue() = runTest {
        datastore.setHiddenMemoriesLockMethod(LockMethod.CUSTOM_PIN)
        assertEquals(LockMethod.CUSTOM_PIN.name, datastore.hiddenMemoriesLockMethod.first())
    }

    @Test
    fun lockDuration_defaultsToOneMinute() = runTest {
        assertEquals(LockDuration.ONE_MINUTE.name, datastore.hiddenMemoriesLockDuration.first())
    }

    @Test
    fun setHiddenMemoriesLockDuration_persistsValue() = runTest {
        datastore.setHiddenMemoriesLockDuration(LockDuration.THIRTY_MINUTES)
        assertEquals(LockDuration.THIRTY_MINUTES.name, datastore.hiddenMemoriesLockDuration.first())
    }

    @Test
    fun isCustomPinSet_falseWhenNoPin() = runTest {
        assertFalse(datastore.isCustomPinSet().first())
    }

    @Test
    fun isCustomPinSet_trueAfterSettingPin() = runTest {
        datastore.setHiddenMemoriesCustomPin("1234")
        assertTrue(datastore.isCustomPinSet().first())
    }

    @Test
    fun isPinCorrect_falseWhenNoPinSet() = runTest {
        assertFalse(datastore.isPinCorrect("1234"))
    }

    @Test
    fun isPinCorrect_trueForMatchingPin() = runTest {
        datastore.setHiddenMemoriesCustomPin("1234")
        assertTrue(datastore.isPinCorrect("1234"))
        assertFalse(datastore.isPinCorrect("0000"))
    }
}
