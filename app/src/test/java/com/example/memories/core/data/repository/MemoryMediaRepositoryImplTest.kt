package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.room.dao.MediaDao
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MemoryMediaRepositoryImplTest {

    private val mediaDao = mockk<MediaDao>(relaxed = true)
    private val repository = MemoryMediaRepositoryImpl(mediaDao)

    @Test
    fun updateOwner_passesOwnerIdToDao() = runTest {
        repository.updateOwner("firebase-uid")

        coVerify { mediaDao.updateOwner("firebase-uid") }
    }
}
