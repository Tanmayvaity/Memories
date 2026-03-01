package com.example.memories.feature.feature_feed.domain.usecase.hidden_usecase

import android.R.attr.type
import androidx.paging.PagingData
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import kotlinx.coroutines.flow.Flow

class GetHiddenFeedUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(query : String): Flow<PagingData<MemoryWithMediaModel>> {
        return repository.getHiddenMemories(query)
    }

}