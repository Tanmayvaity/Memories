package com.example.memories.core.data.data_source.remote

import androidx.paging.PagingSource
import com.example.memories.core.data.data_source.remote.RemoteMediaService
import com.example.memories.core.data.data_source.remote.RemoteVideoPagingSource
import com.example.memories.core.domain.model.RemoteVideoMediaResponse
import com.example.memories.core.domain.model.User
import com.example.memories.core.domain.model.Video
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteVideoPagingSourceTest {

    private val service = mockk<RemoteMediaService>()

    private fun video(id: Long) = Video(
        id = id,
        width = 100,
        height = 100,
        url = "url/$id",
        image = "img/$id",
        duration = 20,
        user = User(id = 1, name = "n", profileUrl = "u"),
        videoFiles = emptyList(),
    )

    private fun response(videos: List<Video>, page: Int, nextPage: String?) =
        RemoteVideoMediaResponse(
            videos = videos,
            page = page,
            prevPage = null,
            nextPage = nextPage,
        )

    private fun refresh(key: Int?, loadSize: Int = 10) =
        PagingSource.LoadParams.Refresh(key, loadSize, false)

    @Test
    fun load_nullKey_defaultsToPageOne() = runTest {
        coEvery { service.getRemoteVideoMediaResponse(page = 1, perPage = 10) } returns
            response(listOf(video(1), video(2)), page = 1, nextPage = "next")

        val source = RemoteVideoPagingSource(service)
        val result = source.load(refresh(null)) as PagingSource.LoadResult.Page

        assertEquals(listOf(1L, 2L), result.data.map { it.id })
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun load_nullNextPage_yieldsNullNextKey() = runTest {
        coEvery { service.getRemoteVideoMediaResponse(page = 2, perPage = 10) } returns
            response(listOf(video(5)), page = 2, nextPage = null)

        val source = RemoteVideoPagingSource(service)
        val result = source.load(refresh(2)) as PagingSource.LoadResult.Page

        assertEquals(1, result.prevKey)
        assertNull(result.nextKey)
    }

    @Test
    fun load_filtersAlreadySeenIdsAcrossPages() = runTest {
        coEvery { service.getRemoteVideoMediaResponse(page = 1, perPage = 10) } returns
            response(listOf(video(1), video(2)), page = 1, nextPage = "next")
        coEvery { service.getRemoteVideoMediaResponse(page = 2, perPage = 10) } returns
            response(listOf(video(2), video(3)), page = 2, nextPage = "next")

        val source = RemoteVideoPagingSource(service)
        source.load(refresh(1))
        val second = source.load(refresh(2)) as PagingSource.LoadResult.Page

        assertEquals(listOf(3L), second.data.map { it.id })
    }

    @Test
    fun load_serviceThrows_returnsError() = runTest {
        val boom = RuntimeException("network down")
        coEvery { service.getRemoteVideoMediaResponse(page = 1, perPage = 10) } throws boom

        val source = RemoteVideoPagingSource(service)
        val result = source.load(refresh(null))

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(boom, (result as PagingSource.LoadResult.Error).throwable)
    }
}
