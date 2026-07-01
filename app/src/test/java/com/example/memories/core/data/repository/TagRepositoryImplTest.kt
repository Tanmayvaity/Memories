package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.data.data_source.room.Entity.TagWithMemoryCount
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.domain.model.TagModel
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.presentation.tags.SortBy
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TagRepositoryImplTest {

    private val tagDao = mockk<TagDao>(relaxed = true)
    private val repository = TagRepositoryImpl(tagDao)

    @Test
    fun insertTag_delegatesToDaoWithEntity() = runTest {
        repository.insertTag(TagModel(tagId = "t1", label = "work"))

        coVerify { tagDao.insertTag(match { it.tagId == "t1" && it.label == "work" }) }
    }

    @Test
    fun fetchTags_mapsEntitiesToDomain() = runTest {
        every { tagDao.getAllTags() } returns flowOf(listOf(TagEntity("t1", "work")))

        val tags = repository.fetchTags().first()

        assertEquals(listOf(TagModel("t1", "work")), tags)
    }

    @Test
    fun getTagByLabel_returnsMappedTag() = runTest {
        io.mockk.coEvery { tagDao.getTagByLabel("work") } returns TagEntity("t1", "work")

        assertEquals(TagModel("t1", "work"), repository.getTagByLabel("work"))
    }

    @Test
    fun getTagByLabel_returnsNullWhenMissing() = runTest {
        io.mockk.coEvery { tagDao.getTagByLabel("none") } returns null

        assertNull(repository.getTagByLabel("none"))
    }

    @Test
    fun deleteTag_delegatesToDao() = runTest {
        repository.deleteTag("t1")

        coVerify { tagDao.deleteTag("t1") }
    }

    // ---- getTagsWithMemoryCount selection matrix ----

    private fun stubAllCountFlows() {
        every { tagDao.getTagsWithMemoryCount() } returns row("countDesc")
        every { tagDao.getTagsWithMemoryCountAscending() } returns row("countAsc")
        every { tagDao.getTagsWithMemoryCountByLabel() } returns row("labelDesc")
        every { tagDao.getTagsWithMemoryCountByLabelAscending() } returns row("labelAsc")
        every { tagDao.getTagsWithMemoryCountBySearch(any()) } returns row("search")
    }

    private fun row(label: String) = flowOf(listOf(TagWithMemoryCount("id", label, 1)))

    @Test
    fun getTagsWithMemoryCount_nonEmptyQuery_usesSearch() = runTest {
        stubAllCountFlows()

        val result = repository.getTagsWithMemoryCount(SortOrder.Descending, SortBy.Count, "trip").first()

        assertEquals("search", result.first().tagLabel)
        verify { tagDao.getTagsWithMemoryCountBySearch("trip") }
    }

    @Test
    fun getTagsWithMemoryCount_countDescending() = runTest {
        stubAllCountFlows()

        val result = repository.getTagsWithMemoryCount(SortOrder.Descending, SortBy.Count, "").first()

        assertEquals("countDesc", result.first().tagLabel)
        verify { tagDao.getTagsWithMemoryCount() }
    }

    @Test
    fun getTagsWithMemoryCount_countAscending() = runTest {
        stubAllCountFlows()

        val result = repository.getTagsWithMemoryCount(SortOrder.Ascending, SortBy.Count, "").first()

        assertEquals("countAsc", result.first().tagLabel)
        verify { tagDao.getTagsWithMemoryCountAscending() }
    }

    @Test
    fun getTagsWithMemoryCount_labelDescending() = runTest {
        stubAllCountFlows()

        val result = repository.getTagsWithMemoryCount(SortOrder.Descending, SortBy.Label, "").first()

        assertEquals("labelDesc", result.first().tagLabel)
        verify { tagDao.getTagsWithMemoryCountByLabel() }
    }

    @Test
    fun getTagsWithMemoryCount_labelAscending() = runTest {
        stubAllCountFlows()

        val result = repository.getTagsWithMemoryCount(SortOrder.Ascending, SortBy.Label, "").first()

        assertEquals("labelAsc", result.first().tagLabel)
        verify { tagDao.getTagsWithMemoryCountByLabelAscending() }
    }
}
