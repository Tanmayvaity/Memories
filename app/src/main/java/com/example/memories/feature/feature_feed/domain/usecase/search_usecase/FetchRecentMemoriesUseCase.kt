package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val RECENT_MEMORIES_NUM = 7

class FetchRecentMemoriesUseCase(
    private val memoryRepository: MemoryRepository
) {

    operator fun invoke(): Flow<List<MemoryWithMediaModel>> {
        return memoryRepository.getRecentMemories(RECENT_MEMORIES_NUM).map { memories ->
            memories.sortedBy { it.memory.memoryForTimeStamp }.reversed()
        }
    }
}