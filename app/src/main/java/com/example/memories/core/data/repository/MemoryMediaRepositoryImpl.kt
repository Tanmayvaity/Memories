package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.domain.repository.MemoryMediaRepository
import javax.inject.Inject

class MemoryMediaRepositoryImpl @Inject constructor(
    private val mediaDao: MediaDao
) : MemoryMediaRepository {

    override suspend fun updateOwner(ownerId: String) {
        mediaDao.updateOwner(ownerId)
    }
}
