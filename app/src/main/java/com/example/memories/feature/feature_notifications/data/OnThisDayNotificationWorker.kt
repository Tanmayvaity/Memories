package com.example.memories.feature.feature_notifications.data

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.util.hasPostNotificationPermission
import com.example.memories.core.util.mapContentUriToType
import com.example.memories.core.util.mapToType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@HiltWorker
class OnThisDayNotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val otherSettingsDatastore: OtherSettingsDatastore,
    val workManager: WorkManager,
    val notificationService: NotificationService,
    val memoryRepository: MemoryRepository,
    val mediaManager: MediaManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "OnThisDayWorker"
        const val ON_THIS_DAY_NOTIFICATION_WORKER = "ON_THIS_DAY_NOTIFICATION_WORKER"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        Log.d(TAG, "doWork: Working work manager work request")


        // check if permission has been granted
        val postNotificationPermissionGranted = appContext.hasPostNotificationPermission()

        if (!postNotificationPermissionGranted) {
            workManager.cancelAllWorkByTag(ON_THIS_DAY_NOTIFICATION_WORKER)
            Log.i(TAG, "doWork: POST_NOTIFICATIONS permission not granted")
            return@withContext Result.success()
        }

        if (!otherSettingsDatastore.onThisDayNotificationAllowed.first()) {
            workManager.cancelAllWorkByTag(ON_THIS_DAY_NOTIFICATION_WORKER)
            Log.i(TAG, "doWork: On this day notifications permission not granted")
            return@withContext Result.success()
        }

        if(!notificationService.isOnThisDayChannelEnabled){
            workManager.cancelAllWorkByTag(ON_THIS_DAY_NOTIFICATION_WORKER)
            Log.i(TAG, "doWork: ${NotificationService.ON_THIS_DAY_CHANNEL} channel disabled by the user")
            return@withContext Result.success()
        }

        val zone = ZoneId.systemDefault()
        val now = LocalDate.now(zone)
            val startOfTheDayTimeStamp = now.atStartOfDay(zone).toInstant().toEpochMilli()
            val endOfTheDayTimeStamp =
                now.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()
        val memory =
            memoryRepository.getMemoriesWithinRange(
                startOfTheDayTimeStamp,
                endOfTheDayTimeStamp
            )
                .firstOrNull()
        if(memory == null){
            Log.i(TAG, "doWork: memory is null , no memory for this day ")
            return@withContext Result.success()
        }

        Log.i(TAG, "doWork: ${memory}")
        val firstMemoryUri = memory?.mediaList?.firstOrNull()?.uri

        val type = if (firstMemoryUri != null) {
            firstMemoryUri.toUri().mapToType()
        } else {
            null
        }
        val bitmap = if (type != null && type.isImageFile() && firstMemoryUri != null) {
            mediaManager.uriToBitmap(firstMemoryUri.toUri())
        } else {
            null
        }

        // show on ths day notification
        notificationService.showOnThisDayNotification(
            bitmap?.getOrNull(),
            "A memory from this day",
            "You have memories to look back on ${now.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}"
        )


        return@withContext Result.success()
    }




}