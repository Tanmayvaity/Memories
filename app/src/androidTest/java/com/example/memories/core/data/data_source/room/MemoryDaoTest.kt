package com.example.memories.core.data.data_source.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import com.example.memories.core.domain.model.Type
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemoryDaoTest {

    private lateinit var db: MemoryDatabase
    private lateinit var dao: MemoryDao
    private lateinit var tagDao: TagDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MemoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = db.memoryDao
        tagDao = db.tagDao
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    @Test
    fun insertMemoryWithMediaAndTag_stampsMemoryIdAndCreatesCrossRefs() = runTest {
        val memory = TestEntities.memory("m1")
        // media supplied with a different memoryId should be re-stamped to the memory's id
        val media = listOf(TestEntities.media("med1", memoryId = "wrong"))
        val tags = listOf(TestEntities.tag("t1", "work"))
        tagDao.insertTags(tags)

        dao.insertMemoryWithMediaAndTag(memory, media, tags)

        val stored = dao.getMemoryById("m1").first()!!
        assertEquals(1, stored.list.size)
        assertEquals("m1", stored.list.first().memoryId)
        assertEquals(listOf("work"), stored.tags.map { it.label })
    }

    @Test
    fun updateMemory_removesMediaNotInIncomingList() = runTest {
        val memory = TestEntities.memory("m1")
        val media = listOf(
            TestEntities.media("med1", "m1", position = 0),
            TestEntities.media("med2", "m1", position = 1),
        )
        dao.insertMemoryWithMediaAndTag(memory, media, emptyList())

        // keep only med1
        dao.updateMemory(memory, listOf(media[0]), emptyList())

        val stored = dao.getMemoryById("m1").first()!!
        assertEquals(listOf("med1"), stored.list.map { it.mediaId })
    }

    @Test
    fun updateMemory_emptyMediaListDeletesAllMedia() = runTest {
        val memory = TestEntities.memory("m1")
        dao.insertMemoryWithMediaAndTag(
            memory,
            listOf(TestEntities.media("med1", "m1")),
            emptyList(),
        )

        dao.updateMemory(memory, emptyList(), emptyList())

        val stored = dao.getMemoryById("m1").first()!!
        assertTrue(stored.list.isEmpty())
    }

    @Test
    fun updateMemory_emptyTagListDeletesAllTags() = runTest {
        val memory = TestEntities.memory("m1")
        val tags = listOf(TestEntities.tag("t1", "work"))
        tagDao.insertTags(tags)
        dao.insertMemoryWithMediaAndTag(memory, emptyList(), tags)

        dao.updateMemory(memory, emptyList(), emptyList())

        val stored = dao.getMemoryById("m1").first()!!
        assertTrue(stored.tags.isEmpty())
    }

    @Test
    fun updateMemory_replacesTagSet() = runTest {
        val memory = TestEntities.memory("m1")
        val work = TestEntities.tag("t1", "work")
        val home = TestEntities.tag("t2", "home")
        tagDao.insertTags(listOf(work, home))
        dao.insertMemoryWithMediaAndTag(memory, emptyList(), listOf(work))

        dao.updateMemory(memory, emptyList(), listOf(home))

        val stored = dao.getMemoryById("m1").first()!!
        assertEquals(listOf("home"), stored.tags.map { it.label })
    }

    @Test
    fun deleteAllHiddenMemories_onlyRemovesHidden() = runTest {
        dao.insertMemory(TestEntities.memory("visible", hidden = false))
        dao.insertMemory(TestEntities.memory("secret", hidden = true))

        dao.deleteAllHiddenMemories()

        assertNull(dao.getMemoryById("secret").first())
        assertTrue(dao.getMemoryById("visible").first() != null)
    }

    @Test
    fun unHideAllMemories_clearsHiddenFlag() = runTest {
        dao.insertMemory(TestEntities.memory("secret", hidden = true))

        dao.unHideAllMemories()

        assertFalse(dao.getMemoryById("secret").first()!!.memory.hidden)
    }

    @Test
    fun updateHiddenAndFavourite_flipFlags() = runTest {
        dao.insertMemory(TestEntities.memory("m1"))

        dao.updateHidden("m1", true)
        dao.updateFavourite("m1", true)

        val stored = dao.getMemoryById("m1").first()!!.memory
        assertTrue(stored.hidden)
        assertTrue(stored.favourite)
    }

    @Test
    fun getRecentMemories_honoursLimitHiddenFilterAndDescOrder() = runTest {
        dao.insertMemory(TestEntities.memory("old", timeStamp = 100))
        dao.insertMemory(TestEntities.memory("mid", timeStamp = 200))
        dao.insertMemory(TestEntities.memory("new", timeStamp = 300))
        dao.insertMemory(TestEntities.memory("hidden", timeStamp = 999, hidden = true))

        val recent = dao.getRecentMemories(limit = 2).first()

        assertEquals(listOf("new", "mid"), recent.map { it.memory.memoryId })
    }

    @Test
    fun getEarliestMemoryTimestamp_ignoresHidden() = runTest {
        dao.insertMemory(TestEntities.memory("hidden", hidden = true, memoryForTimeStamp = 50))
        dao.insertMemory(TestEntities.memory("visible", hidden = false, memoryForTimeStamp = 500))

        assertEquals(500L, dao.getEarliestMemoryTimestamp())
    }

    @Test
    fun getDailyStats_aggregatesCountAndWordCount() = runTest {
        val day = 1_700_000_000_000L // shared timestamp -> same local day bucket
        dao.insertMemory(TestEntities.memory("m1", content = "two words", memoryForTimeStamp = day))
        dao.insertMemory(TestEntities.memory("m2", content = "single", memoryForTimeStamp = day))

        val stats = dao.getDailyStats().first()

        assertEquals(1, stats.size)
        assertEquals(2, stats.first().count)
        assertEquals(3, stats.first().words) // 2 + 1
    }

    @Test
    fun getDailyStats_treatsEmptyContentAsZeroWords() = runTest {
        dao.insertMemory(TestEntities.memory("m1", content = "   ", memoryForTimeStamp = 1_700_000_000_000L))

        val stats = dao.getDailyStats().first()

        assertEquals(1, stats.size)
        assertEquals(0, stats.first().words)
    }

    @Test
    fun getMediaBreakdown_classifiesMutuallyExclusively() = runTest {
        // text only
        dao.insertMemory(TestEntities.memory("text"))
        // photo only
        dao.insertMemoryWithMediaAndTag(
            TestEntities.memory("photo"),
            listOf(TestEntities.media("p1", "photo", type = Type.IMAGE_JPG)),
            emptyList(),
        )
        // video only
        dao.insertMemoryWithMediaAndTag(
            TestEntities.memory("video"),
            listOf(TestEntities.media("v1", "video", type = Type.VIDEO_MP4)),
            emptyList(),
        )
        // mixed -> counts as video
        dao.insertMemoryWithMediaAndTag(
            TestEntities.memory("mixed"),
            listOf(
                TestEntities.media("p2", "mixed", type = Type.IMAGE_PNG),
                TestEntities.media("v2", "mixed", type = Type.VIDEO_MP4),
            ),
            emptyList(),
        )

        val breakdown = dao.getMediaBreakdown().first()

        assertEquals(1, breakdown.textOnly)
        assertEquals(1, breakdown.photo)
        assertEquals(2, breakdown.video)
    }

    @Test
    fun getTotalMemoryCount_excludesHidden() = runTest {
        dao.insertMemory(TestEntities.memory("a"))
        dao.insertMemory(TestEntities.memory("b"))
        dao.insertMemory(TestEntities.memory("c", hidden = true))

        assertEquals(2, dao.getTotalMemoryCount().first())
    }
}
