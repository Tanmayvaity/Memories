package com.example.memories.core.data.data_source.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagDaoTest {

    private lateinit var db: MemoryDatabase
    private lateinit var tagDao: TagDao
    private lateinit var memoryDao: MemoryDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MemoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        tagDao = db.tagDao
        memoryDao = db.memoryDao
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    @Test
    fun getTagByLabel_isCaseInsensitive() = runTest {
        tagDao.insertTag(TestEntities.tag("t1", "Work"))

        assertNotNull(tagDao.getTagByLabel("work"))
        assertNotNull(tagDao.getTagByLabel("WORK"))
        assertNull(tagDao.getTagByLabel("home"))
    }

    @Test
    fun insertTags_ignoresDuplicateIds() = runTest {
        tagDao.insertTag(TestEntities.tag("t1", "Work"))
        tagDao.insertTag(TestEntities.tag("t1", "Renamed"))

        val all = tagDao.getAllTags().first()
        assertEquals(1, all.size)
        assertEquals("Work", all.first().label) // original kept, insert ignored
    }

    @Test
    fun getTagsWithMemoryCount_excludesHiddenMemories() = runTest {
        val work = TestEntities.tag("t1", "work")
        tagDao.insertTag(work)
        // one visible + one hidden memory, both tagged work
        memoryDao.insertMemoryWithMediaAndTag(
            TestEntities.memory("m1", hidden = false), emptyList(), listOf(work)
        )
        memoryDao.insertMemoryWithMediaAndTag(
            TestEntities.memory("m2", hidden = true), emptyList(), listOf(work)
        )

        val counts = tagDao.getTagsWithMemoryCount().first()

        assertEquals(1, counts.size)
        assertEquals("work", counts.first().label)
        assertEquals(1, counts.first().memoryCount) // hidden one not counted
    }

    @Test
    fun getTagsWithMemoryCount_includesTagsWithZeroMemories() = runTest {
        tagDao.insertTag(TestEntities.tag("t1", "unused"))

        val counts = tagDao.getTagsWithMemoryCount().first()

        assertEquals(1, counts.size)
        assertEquals(0, counts.first().memoryCount)
    }
}
