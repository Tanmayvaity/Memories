package com.example.memories.feature.feature_memory.domain.usecase

import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import javax.inject.Inject

class MemoryCreateUseCase @Inject constructor(
    val memoryRepository: MemoryRepository
) {
    operator fun invoke(){
        memoryRepository.createMemory()
    }

}