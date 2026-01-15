package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import androidx.paging.PagingData
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import kotlinx.coroutines.flow.Flow

class GetFeedUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(
        type: FetchType,
        sortType: SortType,
        orderByType: SortOrder
    ): Flow<PagingData<MemoryWithMediaModel>> {
        return repository.getMemories(type, sortType, orderByType)
    }

}