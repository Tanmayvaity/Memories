package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository

class GetMemoryByIdUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(id : String): MemoryWithMediaModel?{
        return repository.getMemoryById(id)
    }

}