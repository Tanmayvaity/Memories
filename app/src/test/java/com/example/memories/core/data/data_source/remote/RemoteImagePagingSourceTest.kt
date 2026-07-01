package com.example.memories.core.data.data_source.remote

import androidx.paging.PagingSource
import com.example.memories.core.data.data_source.remote.RemoteImagePagingSource
import com.example.memories.core.data.data_source.remote.RemoteMediaService
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.RemoteImageMediaResponse
import com.example.memories.core.domain.model.Src
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteImagePagingSourceTest {

    private val service = mockk<RemoteMediaService>()

    private fun photo(id: Long) = Photo(
        id = id,
        width = 100,
        height = 100,
        url = "url/$id",
        photographer = "p",
        photographerUrl = "purl",
        photographerId = 1,
        avgColor = "#fff",
        alt = "alt",
        src = Src(portrait = "portrait/$id"),
    )

    private fun response(photos: List<Photo>, page: Int, nextPage: String?) =
        RemoteImageMediaResponse(
            photos = photos,
            page = page,
            prevPage = null,
            nextPage = nextPage,
        )

    private fun refresh(key: Int?, loadSize: Int = 10) =
        PagingSource.LoadParams.Refresh(key, loadSize, false)

    @Test
    fun load_nullKey_defaultsToPageOneAndNullPrevKey() = runTest {
        coEvery { service.getRemoteImageMediaResponse(page = 1, perPage = 10) } returns
            response(listOf(photo(1), photo(2)), page = 1, nextPage = "next")

        val source = RemoteImagePagingSource(service)
        val result = source.load(refresh(null)) as PagingSource.LoadResult.Page

        assertEquals(listOf(1L, 2L), result.data.map { it.id })
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun load_nullNextPage_yieldsNullNextKey() = runTest {
        coEvery { service.getRemoteImageMediaResponse(page = 3, perPage = 10) } returns
            response(listOf(photo(7)), page = 3, nextPage = null)

        val source = RemoteImagePagingSource(service)
        val result = source.load(refresh(3)) as PagingSource.LoadResult.Page

        assertEquals(2, result.prevKey)
        assertNull(result.nextKey)
    }

    @Test
    fun load_filtersAlreadySeenIdsAcrossPages() = runTest {
        coEvery { service.getRemoteImageMediaResponse(page = 1, perPage = 10) } returns
            response(listOf(photo(1), photo(2)), page = 1, nextPage = "next")
        // page 2 repeats id 2 and introduces id 3 -> only 3 should survive
        coEvery { service.getRemoteImageMediaResponse(page = 2, perPage = 10) } returns
            response(listOf(photo(2), photo(3)), page = 2, nextPage = "next")

        val source = RemoteImagePagingSource(service)
        source.load(refresh(1))
        val second = source.load(refresh(2)) as PagingSource.LoadResult.Page

        assertEquals(listOf(3L), second.data.map { it.id })
    }

    @Test
    fun load_serviceThrows_returnsError() = runTest {
        val boom = RuntimeException("network down")
        coEvery { service.getRemoteImageMediaResponse(page = 1, perPage = 10) } throws boom

        val source = RemoteImagePagingSource(service)
        val result = source.load(refresh(null))

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(boom, (result as PagingSource.LoadResult.Error).throwable)
    }
}
