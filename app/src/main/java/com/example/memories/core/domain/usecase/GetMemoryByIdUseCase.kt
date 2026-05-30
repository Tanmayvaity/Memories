package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class GetMemoryByIdUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(id: String): Flow<MemoryWithMediaModel?> {
        return repository.getMemoryById(id)
    }

}