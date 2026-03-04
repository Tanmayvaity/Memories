package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class FetchRecentSearchUseCase(
    private val repository: RecentSearchRepository
) {
    operator fun invoke(): Flow<List<SearchModel>> {
        return repository.fetchRecentSearch()
    }

    companion object{
        private const val TAG = "FetchRecentSearchUseCase"
    }


}