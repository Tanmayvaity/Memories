package com.example.memories.feature.feature_feed.data.repository

import com.example.memories.core.data.data_source.room.dao.SearchDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(
    val searchDao : SearchDao
) : RecentSearchRepository {
    override suspend fun insertSearch(search: SearchModel) {

        val recent = SearchModel(
            memoryId = search.memoryId,
            timeStamp = System.currentTimeMillis()
        )
        searchDao.insertAndTrim(recent.toEntity())
    }

    override fun fetchRecentSearch(): Flow<List<SearchModel>> {
        return searchDao.fetchRecentSearch().map { searchList -> searchList.map { search -> search.toDomain() } }
    }
}