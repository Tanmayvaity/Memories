package com.example.memories.core.data.data_source.worker

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
import com.example.memories.core.util.mapToType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
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

        if (!notificationService.isOnThisDayChannelEnabled) {
            workManager.cancelAllWorkByTag(ON_THIS_DAY_NOTIFICATION_WORKER)
            Log.i(
                TAG,
                "doWork: ${NotificationService.Companion.ON_THIS_DAY_CHANNEL} channel disabled by the user"
            )
            return@withContext Result.success()
        }
        val today = LocalDate.now()
        val earliestTimestamp =
            memoryRepository.getEarliestMemoryTimeStamp() ?: return@withContext Result.success()

        val earliestYear = Instant.ofEpochMilli(earliestTimestamp)
            .atZone(ZoneId.systemDefault())
            .year
        val currentYear = today.year

        val buckets = mutableListOf<Pair<String, LocalDate>>()

        // Fixed intervals
        buckets.add("1 week ago" to today.minusWeeks(1))
        buckets.add("2 week ago" to today.minusWeeks(2))
        buckets.add("3 week ago" to today.minusWeeks(3))
        buckets.add("1 month ago" to today.minusMonths(1))
        buckets.add("2 month ago" to today.minusMonths(2))
        buckets.add("3 month ago" to today.minusMonths(3))
        buckets.add("6 months ago" to today.minusMonths(6))

        // Dynamic years
        for (yearsAgo in 1..(currentYear - earliestYear)) {
            val label = if (yearsAgo == 1) "1 year ago" else "$yearsAgo years ago"
            buckets.add(label to today.minusYears(yearsAgo.toLong()))
        }

        buckets.mapNotNull { (label, targetDate) ->
            val startOfDay = targetDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val endOfDay = targetDate.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()

            val memories = memoryRepository.getMemoriesWithinRange(startOfDay, endOfDay).first()
            if (memories.isNotEmpty()) {
                val first = memories[0]

                val bitmap = if (first.mediaList.isNotEmpty()) {
                    val uri = first.mediaList[0].uri
                    val isTypeImage = uri.toUri().mapToType().isImageFile()

                    if (isTypeImage) mediaManager.uriToBitmap(uri.toUri()).getOrNull() else null
                } else null

                notificationService.showOnThisDayNotification(
                    bitmap,
                    "A memory from this day",
                    "You have memories to look back on ${
                        today.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMM yyyy"
                            )
                        )
                    }"
                )
                return@withContext Result.success()
            }
        }



        return@withContext Result.success()
    }


}