package com.example.memories.feature.feature_memory.data.repository

import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import javax.inject.Inject


class MemoryRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
    val memoryDao: MemoryDao,
    val mediaDao : MediaDao
) : MemoryRepository {
    override suspend fun saveToInternalStorage(uriList: List<Uri>): Result<List<Uri>> {
        return mediaManager.saveToInternalStorage(uriList)
    }

    override suspend fun insertMemory(memory: MemoryModel) {
        memoryDao.insertMemory(memory.toEntity())
    }

    override suspend fun insertMedia(mediaList: List<MediaModel>) {
        mediaDao.insertAllMedia(mediaList.map { media -> media.toEntity() })
    }


}

