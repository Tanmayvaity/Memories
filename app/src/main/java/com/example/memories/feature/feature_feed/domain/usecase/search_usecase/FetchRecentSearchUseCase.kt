package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class FetchRecentSearchUseCase(
    private val repository: RecentSearchRepository
) {
    operator fun invoke(): Result<Flow<List<SearchModel>>> {
        try {
            repository.fetchRecentSearch().also { result ->
                return Result.Success(result)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while fetching recent search from Search Entity $e" )
            return Result.Error(e)

        }
    }

    companion object{
        private const val TAG = "FetchRecentSearchUseCase"
    }


}