package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.core.domain.model.SearchModel
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {

    suspend fun insertSearch(search : SearchModel)

    fun fetchRecentSearch() : Flow<List<SearchModel>>

    suspend fun deleteSearchById(id : String)

    suspend fun deleteAllSearch()

}