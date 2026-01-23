package com.example.memories.feature.feature_other.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.memories.feature.feature_other.data.worker.DeleteAllDataWorker
import com.example.memories.feature.feature_other.domain.model.DeletionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeleteAllDataServiceImpl @Inject constructor(
    private val workManager : WorkManager
)  {
    fun deleteAllData() : Flow<DeletionStatus>{
        val request = OneTimeWorkRequestBuilder<DeleteAllDataWorker>()
            .addTag(DeleteAllDataWorker.DELETE_ALL_DATA_WORKER)
            .build()
        workManager.enqueueUniqueWork(
            DeleteAllDataWorker.DELETE_ALL_DATA_WORKER,
            ExistingWorkPolicy.KEEP,
            request
        )

        return workManager.getWorkInfoByIdFlow(request.id).map { workInfo ->
            when(workInfo?.state){
                WorkInfo.State.RUNNING -> DeletionStatus.InProgress(workInfo.progress.getString("step"))
                WorkInfo.State.SUCCEEDED -> DeletionStatus.Success
                WorkInfo.State.FAILED -> DeletionStatus.Error("Deletion failed")
                else -> DeletionStatus.Idle
            }
        }
    }
}