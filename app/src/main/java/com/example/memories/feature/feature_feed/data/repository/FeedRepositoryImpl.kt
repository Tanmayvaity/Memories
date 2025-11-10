package com.example.memories.feature.feature_feed.data.repository

import android.util.Log
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.nio.file.Files.isHidden
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    val memoryDao : MemoryDao
): FeedRepository {

    companion object{
        private const val TAG = "FeedRepository"
    }

    override suspend fun getMemories(type : FetchType): Flow<List<MemoryWithMediaModel>> {
        return when(type) {
            FetchType.ALL -> memoryDao.getAllMemoriesWithMedia()
            FetchType.FAVORITE -> memoryDao.getAllFavouriteMemoriesWithMedia()
            FetchType.HIDDEN -> memoryDao.getAllHiddenMemoriesWithMedia()
        }.map { memoryList -> memoryList.map { it -> it.toDomain() } }
    }

    override suspend fun updateFavouriteState(id: String, isFavourite: Boolean) {
        Log.d(TAG, "updateFavouriteState: called")
        return memoryDao.updateFavourite(id,isFavourite)
    }

    override suspend fun updateHiddenState(id: String, isHidden: Boolean) {
        Log.d(TAG, "updateHiddenState: called")
        return memoryDao.updateHidden(id,isHidden)
    }

    override suspend fun getMemoryById(id: String): MemoryWithMediaModel? {
        return memoryDao.getMemoryById(id)?.toDomain()
    }

    override suspend fun delete(memory: MemoryModel): Int {
        return memoryDao.deleteMemory(memory.toEntity())
    }

    override suspend fun getMemoryByTitle(query: String): Flow<List<MemoryWithMediaModel>> {
        return memoryDao.getAllMemoriesWithMediaByTitle(query).map { memoryList ->
            memoryList.map { it -> it.toDomain() }
        }
    }


}