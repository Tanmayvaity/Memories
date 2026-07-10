package com.example.memories.core.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.testing.asSnapshot
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.remote.RemoteMediaService
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.RemoteImageMediaResponse
import com.example.memories.core.domain.model.RemoteVideoMediaResponse
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.Src
import com.example.memories.core.domain.model.User
import com.example.memories.core.domain.model.Video
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class MediaRepositoryImplTest {

    private val mediaManager = mockk<MediaManager>(relaxed = true)
    private val remoteMediaService = mockk<RemoteMediaService>()
    private val repository = MediaRepositoryImpl(mediaManager, remoteMediaService)

    @Test
    fun uriToBitmap_passesThroughManagerResult() = runTest {
        val uri = mockk<Uri>()
        val expected = Result.Success(mockk<Bitmap>())
        coEvery { mediaManager.uriToBitmap(uri, 90f) } returns expected

        assertSame(expected, repository.uriToBitmap(uri, 90f))
    }

    @Test
    fun saveBitmapToInternalStorage_passesThrough() = runTest {
        val bitmap = mockk<Bitmap>()
        val expected = Result.Success(mockk<Uri>())
        coEvery { mediaManager.saveBitmapToInternalStorage(bitmap) } returns expected

        assertSame(expected, repository.saveBitmapToInternalStorage(bitmap))
    }

    @Test
    fun saveToCacheStorage_delegatesToManager() = runTest {
        val uri = mockk<Uri>()
        val bitmap = mockk<Bitmap>()
        coEvery { mediaManager.saveToCacheStorage(uri, bitmap) } returns Result.Success(null)

        repository.saveToCacheStorage(uri, bitmap)

        coVerify { mediaManager.saveToCacheStorage(uri, bitmap) }
    }

    @Test
    fun saveRemoteMediaToCache_delegatesToManager() = runTest {
        coEvery { mediaManager.saveRemoteMediaToCache("http://x/y.jpg", true) } returns Result.Success(null)

        repository.saveRemoteMediaToCache("http://x/y.jpg", true)

        coVerify { mediaManager.saveRemoteMediaToCache("http://x/y.jpg", true) }
    }

    @Test
    fun deleteMedia_delegatesToManager() = runTest {
        val uris = listOf(mockk<Uri>())
        coEvery { mediaManager.deleteInternalMedia(uris) } returns Result.Success("ok")

        assertEquals("ok", (repository.deleteMedia(uris) as Result.Success).data)
    }

    @Test
    fun generateShareableUri_passesThrough() = runTest{
        val out = mockk<Uri>()
        coEvery { mediaManager.generateShareableUri(true, null) } returns out

        assertSame(out, repository.generateShareableUri(true, null))
    }

    @Test
    fun getRemoteImages_emitsPhotosFromService() = runTest {
        coEvery { remoteMediaService.getRemoteImageMediaResponse(page = 1, perPage = any()) } returns
            RemoteImageMediaResponse(
                photos = listOf(photo(1), photo(2)),
                page = 1,
                prevPage = null,
                nextPage = null,
            )

        val snapshot = repository.getRemoteImages().asSnapshot()

        assertEquals(listOf(1L, 2L), snapshot.map { it.id })
    }

    @Test
    fun getRemoteVideos_emitsVideosFromService() = runTest {
        coEvery { remoteMediaService.getRemoteVideoMediaResponse(page = 1, perPage = any()) } returns
            RemoteVideoMediaResponse(
                videos = listOf(video(1)),
                page = 1,
                prevPage = null,
                nextPage = null,
            )

        val snapshot = repository.getRemoteVideos().asSnapshot()

        assertEquals(listOf(1L), snapshot.map { it.id })
    }

    private fun photo(id: Long) = Photo(
        id = id, width = 1, height = 1, url = "u", photographer = "p",
        photographerUrl = "pu", photographerId = 1, avgColor = "#fff",
        alt = "alt", src = Src(portrait = "portrait"),
    )

    private fun video(id: Long) = Video(
        id = id, width = 1, height = 1, url = "u", image = "img", duration = 20,
        user = User(id = 1, name = "n", profileUrl = "u"), videoFiles = emptyList(),
    )
}
