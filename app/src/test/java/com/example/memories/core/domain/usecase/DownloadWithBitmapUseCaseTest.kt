package com.example.memories.core.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DownloadWithBitmapUseCaseTest {

    private val repository = mockk<MediaRepository>(relaxed = true)
    private val useCase = DownloadWithBitmapUseCase(repository)

    @Test
    fun nullUri_returnsError() = runTest {
        val result = useCase(uri = null, shaderCode = null)

        assertTrue(result is Result.Error)
        assertEquals("Uri is null", (result as Result.Error).error.message)
    }

    @Test
    fun noShader_downloadsBitmapDecodedFromUri() = runTest {
        val uri = mockk<Uri>()
        val bitmap = mockk<Bitmap>()
        coEvery { repository.uriToBitmap(uri, 0f) } returns Result.Success(bitmap)
        coEvery { repository.downloadWithBitmap(bitmap) } returns Result.Success("/path.jpg")

        val result = useCase(uri = uri, shaderCode = null)

        assertEquals("/path.jpg", (result as Result.Success).data)
        coVerify { repository.downloadWithBitmap(bitmap) }
    }

    @Test
    fun noShader_nullDecodedBitmap_returnsError() = runTest {
        val uri = mockk<Uri>()
        coEvery { repository.uriToBitmap(uri, 0f) } returns Result.Success(null)

        val result = useCase(uri = uri, shaderCode = null)

        assertTrue(result is Result.Error)
        assertEquals("Bitmap is null", (result as Result.Error).error.message)
        coVerify(exactly = 0) { repository.downloadWithBitmap(any()) }
    }

    @Test
    fun withShader_appliesFilterThenDownloads() = runTest {
        val uri = mockk<Uri>()
        val filtered = mockk<Bitmap>()
        coEvery { repository.applyFilterToUri(uri, "shader", 0f) } returns filtered
        coEvery { repository.downloadWithBitmap(filtered) } returns Result.Success("/filtered.jpg")

        val result = useCase(uri = uri, shaderCode = "shader")

        assertEquals("/filtered.jpg", (result as Result.Success).data)
        coVerify { repository.applyFilterToUri(uri, "shader", 0f) }
    }

    @Test
    fun withShader_nullFilteredBitmap_returnsError() = runTest {
        val uri = mockk<Uri>()
        coEvery { repository.applyFilterToUri(uri, "shader", 0f) } returns null

        val result = useCase(uri = uri, shaderCode = "shader")

        assertTrue(result is Result.Error)
        assertEquals("Bitmap is null", (result as Result.Error).error.message)
        coVerify(exactly = 0) { repository.downloadWithBitmap(any()) }
    }
}
