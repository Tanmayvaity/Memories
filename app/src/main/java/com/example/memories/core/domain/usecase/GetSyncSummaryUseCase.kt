package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.SyncSummary
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Emits `null` while nobody is signed in (always the case in the `base` flavor), otherwise the
 * signed-in account's pending sync count.
 */
class GetSyncSummaryUseCase(
    private val appSettingRepository: AppSettingRepository,
    private val memoryRepository: MemoryRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<SyncSummary?> =
        appSettingRepository.currentUser.flatMapLatest { userId ->
            if (userId == LOCAL_OWNER) {
                flowOf(null)
            } else {
                // TODO: set isUploading from the uploader once remote upload is implemented.
                memoryRepository.getPendingSyncCount(userId).map { SyncSummary(pendingCount = it) }
            }
        }
}
