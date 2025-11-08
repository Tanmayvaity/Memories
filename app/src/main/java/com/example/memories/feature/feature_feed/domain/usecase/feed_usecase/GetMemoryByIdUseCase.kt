package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository

class GetMemoryByIdUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(id : String): MemoryWithMediaModel?{
        return repository.getMemoryById(id)
    }

}