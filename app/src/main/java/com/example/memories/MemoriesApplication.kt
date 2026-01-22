package com.example.memories

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.core.domain.usecase.InvokeNotificationUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MemoriesApplication : Application(), Configuration.Provider{

    @Inject lateinit var workerFactory : HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()


    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val onThisDayReminderChannel = NotificationChannel(
            NotificationService.ON_THIS_DAY_CHANNEL,
            "On This Day Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminds you of your previous memories on this day"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val dailyReminderChannel = NotificationChannel(
            NotificationService.DAILY_REMINDER_CHANNEL,
            "Daily Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to create new memory"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannels(
            listOf(onThisDayReminderChannel,dailyReminderChannel)
        )

    }


}