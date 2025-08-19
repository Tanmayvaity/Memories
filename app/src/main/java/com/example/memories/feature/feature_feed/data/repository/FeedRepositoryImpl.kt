package com.example.memories.feature.feature_feed.data.repository

import android.util.Log
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import java.nio.file.Files.isHidden
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    val memoryDao : MemoryDao
): FeedRepository {

    companion object{
        private const val TAG = "FeedRepository"
    }

    override suspend fun getMemories(): List<MemoryWithMediaModel> {
        return memoryDao.getAllMemoriesWithMedia().map { it -> it.toDomain() }
    }

    override suspend fun updateFavouriteState(id: String, isFavourite: Boolean) {
        Log.d(TAG, "updateFavouriteState: called")
        return memoryDao.updateFavourite(id,isFavourite)
    }

    override suspend fun updateHiddenState(id: String, isHidden: Boolean) {
        Log.d(TAG, "updateHiddenState: called")
        return memoryDao.updateHidden(id,isHidden)
    }

}