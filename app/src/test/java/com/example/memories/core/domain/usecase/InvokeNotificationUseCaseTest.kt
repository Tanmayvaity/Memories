package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryNotificationScheduler
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InvokeNotificationUseCaseTest {

    private val repository = mockk<AppSettingRepository>(relaxed = true)
    private val scheduler = mockk<MemoryNotificationScheduler>(relaxed = true)
    private val useCase = InvokeNotificationUseCase(repository, scheduler)

    @Test
    fun schedulesAlarmWithDecomposedTime_whenAllReminderConditionsMet() = runTest {
        every { scheduler.isReminderChannelAllowed } returns true
        every { scheduler.canAlarmSchedule } returns true
        every { repository.reminderNotificationAllowed } returns flowOf(true)
        every { repository.reminderTime } returns flowOf(7 * 60 + 30) // 450 -> 7h 30m
        // on-this-day disabled
        every { scheduler.isOnThisDayChannelAllowed } returns false
        every { repository.onThisDayNotificationAllowed } returns flowOf(true)

        useCase()

        coVerify { scheduler.scheduleAlarm(7, 30) }
        coVerify(exactly = 0) { scheduler.cancelAlarm() }
        coVerify { scheduler.cancelWork() }
        coVerify(exactly = 0) { scheduler.scheduleWork() }
    }

    @Test
    fun cancelsAlarm_whenAlarmCannotBeScheduled_andSchedulesWorkWhenAllowed() = runTest {
        every { scheduler.isReminderChannelAllowed } returns true
        every { scheduler.canAlarmSchedule } returns false // blocks reminder
        every { repository.reminderNotificationAllowed } returns flowOf(true)
        every { repository.reminderTime } returns flowOf(450)
        every { scheduler.isOnThisDayChannelAllowed } returns true
        every { repository.onThisDayNotificationAllowed } returns flowOf(true)

        useCase()

        coVerify { scheduler.cancelAlarm() }
        coVerify(exactly = 0) { scheduler.scheduleAlarm(any(), any()) }
        coVerify { scheduler.scheduleWork() }
        coVerify(exactly = 0) { scheduler.cancelWork() }
    }

    @Test
    fun cancelsWork_whenChannelAllowedButUserDisabledOnThisDay() = runTest {
        every { scheduler.isReminderChannelAllowed } returns false
        every { repository.reminderNotificationAllowed } returns flowOf(false)
        every { scheduler.isOnThisDayChannelAllowed } returns true
        every { repository.onThisDayNotificationAllowed } returns flowOf(false) // user disabled

        useCase()

        coVerify { scheduler.cancelWork() }
        coVerify(exactly = 0) { scheduler.scheduleWork() }
    }
}
