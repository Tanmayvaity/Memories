package com.example.memories.feature.feature_memory.domain.usecase

import com.example.memories.core.domain.model.TagModel
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTagsByLabelUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository
) {
    suspend operator fun invoke(label : String) : Flow<List<TagModel>> {
        return memoryRepository.fetchTagsByLabel(label)
    }

}