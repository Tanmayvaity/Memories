package com.example.memories.core.domain.usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryMediaRepository
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.domain.repository.TagRepository
import kotlin.coroutines.cancellation.CancellationException

class UpdateCurrentUserUseCase(
    private val appSettingRepository: AppSettingRepository,
    private val memoryRepository: MemoryRepository,
    private val memoryMediaRepository: MemoryMediaRepository,
    private val tagRepository: TagRepository,
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return runCatching {
            appSettingRepository.updateCurrentUser(userId)
            memoryRepository.updateOwner(userId)
            memoryMediaRepository.updateOwner(userId)
            tagRepository.updateOwner(userId)
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { e ->
                if (e is CancellationException) throw e
                Log.e(TAG, "error while updating current user to $userId error : ${e.message}")
                Result.Error(e)
            }
        )
    }

    companion object {
        private const val TAG = "UpdateCurrentUserUseCase"
    }
}
