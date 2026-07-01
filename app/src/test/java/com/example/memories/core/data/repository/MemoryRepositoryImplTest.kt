package com.example.memories.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Type
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryRepositoryImplTest {

    private val mediaManager = mockk<MediaManager>(relaxed = true)
    private val memoryDao = mockk<MemoryDao>(relaxed = true)
    private val tagDao = mockk<TagDao>(relaxed = true)
    private val mediaDao = mockk<MediaDao>(relaxed = true)

    private val repository = MemoryRepositoryImpl(mediaManager, memoryDao, tagDao, mediaDao)

    // ---------- delegation + mapping (non-paging) ----------

    @Test
    fun getMemoryById_mapsEntityToDomain() = runTest {
        every { memoryDao.getMemoryById("m1") } returns flowOf(memoryWithMedia("m1"))

        val result = repository.getMemoryById("m1").first()

        assertEquals("m1", result?.memory?.memoryId)
    }

    @Test
    fun getMemoryById_nullPassesThrough() = runTest {
        every { memoryDao.getMemoryById("m1") } returns flowOf(null)

        assertNull(repository.getMemoryById("m1").first())
    }

    @Test
    fun getRecentMemories_mapsList() = runTest {
        every { memoryDao.getRecentMemories(2) } returns
            flowOf(listOf(memoryWithMedia("a"), memoryWithMedia("b")))

        val result = repository.getRecentMemories(2).first()

        assertEquals(listOf("a", "b"), result.map { it.memory.memoryId })
    }

    @Test
    fun getMemoriesWithinRange_mapsList() = runTest {
        every { memoryDao.getMemoriesBetweenTimestamps(0, 10) } returns
            flowOf(listOf(memoryWithMedia("a")))

        val result = repository.getMemoriesWithinRange(0, 10).first()

        assertEquals(listOf("a"), result.map { it.memory.memoryId })
    }

    @Test
    fun getEarliestMemoryTimeStamp_passesThrough() = runTest {
        coEvery { memoryDao.getEarliestMemoryTimestamp() } returns 1234L

        assertEquals(1234L, repository.getEarliestMemoryTimeStamp())
    }

    @Test
    fun analyticsFlows_arePassedThroughUnchanged() {
        val daily = flowOf(emptyList<com.example.memories.core.domain.model.DailyStat>())
        val breakdown = flowOf(com.example.memories.core.domain.model.MediaBreakdown(0, 0, 0))
        val total = flowOf(0)
        every { memoryDao.getDailyStats() } returns daily
        every { memoryDao.getMediaBreakdown() } returns breakdown
        every { memoryDao.getTotalMemoryCount() } returns total

        assertSame(daily, repository.getDailyStats())
        assertSame(breakdown, repository.getMediaBreakdown())
        assertSame(total, repository.getTotalMemoryCount())
    }

    // ---------- suspend delegations ----------

    @Test
    fun insertMemory_delegatesWithMappedEntity() = runTest {
        repository.insertMemory(MemoryModel(memoryId = "m1", title = "t", content = "c", memoryForTimeStamp = 1L))

        coVerify { memoryDao.insertMemory(match { it.memoryId == "m1" }) }
    }

    @Test
    fun updateFavouriteAndHiddenState_delegate() = runTest {
        repository.updateFavouriteState("m1", true)
        repository.updateHiddenState("m1", false)

        coVerify { memoryDao.updateFavourite("m1", true) }
        coVerify { memoryDao.updateHidden("m1", false) }
    }

    @Test
    fun updateMediaFavouriteState_delegatesToMediaDao() = runTest {
        repository.updateMediaFavouriteState("med1", true)

        coVerify { mediaDao.updateMediaFavourite("med1", true) }
    }

    @Test
    fun deleteAllHiddenAndUnhide_delegate() = runTest {
        repository.deleteAllHiddenMemories()
        repository.unHideAllHiddenMemories()

        coVerify { memoryDao.deleteAllHiddenMemories() }
        coVerify { memoryDao.unHideAllMemories() }
    }

    @Test
    fun saveToInternalStorage_passesThroughMediaManagerResult() = runTest {
        val expected = Result.Success<List<android.net.Uri>>(emptyList())
        coEvery { mediaManager.saveToInternalStorage(any()) } returns expected

        assertSame(expected, repository.saveToInternalStorage(emptyList()))
    }

    @Test
    fun getMediaUrisToDelete_passesThrough() = runTest {
        coEvery { memoryDao.getMediaUrisToDelete("m1", listOf("a")) } returns listOf("uri")

        assertEquals(listOf("uri"), repository.getMediaUrisToDelete("m1", listOf("a")))
    }

    // ---------- getMemories paging-source selection matrix ----------

    @Test
    fun getMemories_all_dateAdded_descending_usesDefaultDao() = runTest {
        every { memoryDao.getAllMemoriesWithMedia() } returns
            fakePagingSource(listOf(memoryWithMedia("m1")))

        val snapshot = repository.getMemories(
            FetchType.ALL, SortType.DateAdded, SortOrder.Descending
        ).asSnapshot()

        assertEquals(listOf("m1"), snapshot.map { it.memory.memoryId })
        verify { memoryDao.getAllMemoriesWithMedia() }
        verify(exactly = 0) { memoryDao.getAllMemoriesWithMediaAscending() }
    }

    @Test
    fun getMemories_all_title_ascending_usesTitleAscendingDao() = runTest {
        every { memoryDao.getAllMemoriesWithMediaByTitleAscending() } returns
            fakePagingSource(listOf(memoryWithMedia("m1")))

        repository.getMemories(FetchType.ALL, SortType.Title, SortOrder.Ascending).asSnapshot()

        verify { memoryDao.getAllMemoriesWithMediaByTitleAscending() }
    }

    @Test
    fun getMemories_favorite_createdForDate_descending_usesFavouriteByMemoryForTimeStampDao() = runTest {
        every { memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStamp() } returns
            fakePagingSource(listOf(memoryWithMedia("m1")))

        repository.getMemories(
            FetchType.FAVORITE, SortType.CreatedForDate, SortOrder.Descending
        ).asSnapshot()

        verify { memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStamp() }
    }

    @Test
    fun getMemories_mapsPagedEntitiesToDomain() = runTest {
        every { memoryDao.getAllMemoriesWithMedia() } returns
            fakePagingSource(listOf(memoryWithMedia("m1", mediaId = "med1")))

        val snapshot = repository.getMemories(
            FetchType.ALL, SortType.DateAdded, SortOrder.Descending
        ).asSnapshot()

        assertEquals(1, snapshot.size)
        assertTrue(snapshot.first().mediaList.any { it.mediaId == "med1" })
    }

    // ---------- helpers ----------

    private fun memoryWithMedia(id: String, mediaId: String? = null) = MemoryWithMedia(
        memory = MemoryEntity(
            memoryId = id,
            title = "Title $id",
            content = "content",
            hidden = false,
            favourite = false,
            timeStamp = 0L,
            longitude = null,
            latitude = null,
            memoryForTimeStamp = 0L,
        ),
        list = if (mediaId == null) emptyList() else listOf(
            MediaEntity(
                mediaId = mediaId,
                memoryId = id,
                uri = "content://$mediaId",
                hidden = false,
                favourite = false,
                timeStamp = 0L,
                longitude = null,
                latitude = null,
                position = 0,
                type = Type.IMAGE_JPG,
            )
        ),
        tags = emptyList(),
    )

    private fun fakePagingSource(items: List<MemoryWithMedia>) =
        object : PagingSource<Int, MemoryWithMedia>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemoryWithMedia> =
                LoadResult.Page(data = items, prevKey = null, nextKey = null)

            override fun getRefreshKey(state: PagingState<Int, MemoryWithMedia>): Int? = null
        }
}
