package com.example.memories.feature.feature_feed.data.repository

import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    val memoryDao : MemoryDao
): FeedRepository {
    override suspend fun getMemories(): List<MemoryWithMediaModel> {
        return memoryDao.getAllMemoriesWithMedia().map { it -> it.toDomain() }
    }

}