package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.util.Log
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class FetchMemoryByIdsUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(ids : List<String>): Flow<List<MemoryWithMediaModel>> {
        return repository.getMemoriesByIds(ids)
    }

    companion object{
        private const val TAG = "FetchMemoryByIdsUseCase"
    }


}