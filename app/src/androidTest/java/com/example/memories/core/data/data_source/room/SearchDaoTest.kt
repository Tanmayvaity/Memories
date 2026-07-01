package com.example.memories.core.data.data_source.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.SearchDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchDaoTest {

    private lateinit var db: MemoryDatabase
    private lateinit var searchDao: SearchDao
    private lateinit var memoryDao: MemoryDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MemoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        searchDao = db.searchDao
        memoryDao = db.memoryDao
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    @Test
    fun fetchRecentSearch_returnsDescendingByTimeStamp() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        memoryDao.insertMemory(TestEntities.memory("m2"))
        searchDao.insertSearch(TestEntities.search("m1", timeStamp = 100))
        searchDao.insertSearch(TestEntities.search("m2", timeStamp = 200))

        val recents = searchDao.fetchRecentSearch().first()

        assertEquals(listOf("m2", "m1"), recents.map { it.memoryId })
    }

    @Test
    fun insertAndTrim_keepsOnlyLatestTwenty() = runTest {
        // 25 memories + searches; only the 20 most recent searches should remain
        repeat(25) { i ->
            memoryDao.insertMemory(TestEntities.memory("m$i"))
        }
        repeat(25) { i ->
            searchDao.insertAndTrim(TestEntities.search("m$i", timeStamp = i.toLong()))
        }

        val recents = searchDao.fetchRecentSearch().first()

        assertEquals(20, recents.size)
        // newest first; oldest five (m0..m4) trimmed away
        assertEquals("m24", recents.first().memoryId)
        assertEquals("m5", recents.last().memoryId)
    }

    @Test
    fun deleteAllSearch_clearsTable() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        searchDao.insertSearch(TestEntities.search("m1", timeStamp = 1))

        searchDao.deleteAllSearch()

        assertEquals(0, searchDao.fetchRecentSearch().first().size)
    }
}
