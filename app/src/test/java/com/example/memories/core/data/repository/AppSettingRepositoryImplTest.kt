package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSettingRepositoryImplTest {

    private val datastore = mockk<OtherSettingsDatastore>(relaxed = true)

    private fun repository() = AppSettingRepositoryImpl(datastore)

    @Test
    fun readFlows_delegateToDatastore() = runTest {
        every { datastore.hiddenMemoriesLockMethod } returns flowOf(LockMethod.CUSTOM_PIN.name)
        every { datastore.hiddenMemoriesLockDuration } returns flowOf(LockDuration.THIRTY_MINUTES.name)
        every { datastore.allNotificationAllowed } returns flowOf(false)
        every { datastore.reminderNotificationAllowed } returns flowOf(true)
        every { datastore.onThisDayNotificationAllowed } returns flowOf(false)
        every { datastore.reminderTime } returns flowOf(450)
        every { datastore.isDarkModeEnabled } returns flowOf(true)
        every { datastore.isCustomPinSet() } returns flowOf(true)

        val repo = repository()

        assertEquals(LockMethod.CUSTOM_PIN.name, repo.lockMethod.first())
        assertEquals(LockMethod.CUSTOM_PIN.name, repo.hiddenMemoryLockMethod.first())
        assertEquals(LockDuration.THIRTY_MINUTES.name, repo.hiddenMemoryLockDuration.first())
        assertFalse(repo.allNotificationAllowed.first())
        assertTrue(repo.reminderNotificationAllowed.first())
        assertFalse(repo.onThisDayNotificationAllowed.first())
        assertEquals(450, repo.reminderTime.first())
        assertTrue(repo.isDarkModeEnabled.first())
        assertTrue(repo.isCustomPinSet.first())
    }

    @Test
    fun writeMethods_delegateToDatastore() = runTest {
        val repo = repository()

        repo.setDarkMode(true)
        repo.enableAllNotifications(false)
        repo.enableReminderNotification(false)
        repo.enableOnThisDayNotification(true)
        repo.setReminderTime(7, 30)
        repo.setHiddenMemoryLockMethod(LockMethod.CUSTOM_PIN)
        repo.setHiddenMemoryLockDuration(LockDuration.ONE_MINUTE)
        repo.setHiddenMemoryCustomPin("1234")

        coVerify { datastore.setDarkMode(true) }
        coVerify { datastore.enableAllNotifications(false) }
        coVerify { datastore.enableReminderNotification(false) }
        coVerify { datastore.enableOnThisDayNotification(true) }
        coVerify { datastore.setReminderTime(7, 30) }
        coVerify { datastore.setHiddenMemoriesLockMethod(LockMethod.CUSTOM_PIN) }
        coVerify { datastore.setHiddenMemoriesLockDuration(LockDuration.ONE_MINUTE) }
        coVerify { datastore.setHiddenMemoriesCustomPin("1234") }
    }

    @Test
    fun isCustomPinCorrect_returnsDatastoreResult() = runTest {
        coEvery { datastore.isPinCorrect("1234") } returns true
        coEvery { datastore.isPinCorrect("0000") } returns false

        val repo = repository()

        assertTrue(repo.isCustomPinCorrect("1234"))
        assertFalse(repo.isCustomPinCorrect("0000"))
    }
}
