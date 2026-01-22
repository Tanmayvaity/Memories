package com.example.memories.core.domain.repository

interface MemoryNotificationScheduler {

    val isReminderChannelAllowed : Boolean
    val isOnThisDayChannelAllowed : Boolean

    val canAlarmSchedule : Boolean
    fun scheduleWork()
    fun cancelWork()

    fun scheduleAlarm(hour : Int, minute : Int)

    fun cancelAlarm()

}