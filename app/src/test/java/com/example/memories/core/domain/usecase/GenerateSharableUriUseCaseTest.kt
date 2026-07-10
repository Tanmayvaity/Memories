package com.example.memories.core.domain.usecase

import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.MediaRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateSharableUriUseCaseTest {

    private val repository = mockk<MediaRepository>(relaxed = true)
    private val useCase = GenerateSharableUriUseCase(repository)

    @Test
    fun returnsSuccessWrappingRepositoryUri() = runTest{
        val uri = mockk<Uri>()
        coEvery { repository.generateShareableUri(true, null) } returns uri

        val result = useCase(isImage = true, uri = null)

        assertTrue(result is Result.Success)
        assertSame(uri, (result as Result.Success).data)
    }

    @Test
    fun returnsErrorWhenRepositoryThrows() = runTest{
        coEvery { repository.generateShareableUri(any(), any()) } throws RuntimeException("no provider")

        val result = useCase(isImage = false, uri = null)

        assertTrue(result is Result.Error)
    }
}
