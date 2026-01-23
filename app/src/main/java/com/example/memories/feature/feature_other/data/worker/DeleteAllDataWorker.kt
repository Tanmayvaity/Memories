package com.example.memories.feature.feature_other.data.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteAllDataWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val mediaDao: MediaDao,
    val mediaManager: MediaManager,
    val db: MemoryDatabase
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val DELETE_ALL_DATA_WORKER = "DELETE_ALL_DATA_WORKER"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            runCatching {
                val uriList = mediaDao.getAllMedia().map { it -> it.uri.toUri() }
                db.clearAllTables()
                mediaManager.deleteInternalMedia(uriList)
            }.fold(
                onSuccess = { Result.success() },
                onFailure = { Result.failure() }
            )
        }

    }

}