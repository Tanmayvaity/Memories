package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class GetFeedUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(type : FetchType): Flow<List<MemoryWithMediaModel>>{
        return repository.getMemories(type)
    }

}