package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMemoryByIdUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(id: String): Flow<MemoryWithMediaModel?> {
        return repository.getMemoryById(id)
    }

}