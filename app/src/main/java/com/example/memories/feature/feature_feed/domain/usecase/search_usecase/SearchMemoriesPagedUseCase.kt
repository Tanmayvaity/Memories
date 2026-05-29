package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import androidx.paging.PagingData
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class SearchMemoriesPagedUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(query: String): Flow<PagingData<MemoryWithMediaModel>> {
        return repository.searchMemories(query)
    }
}
