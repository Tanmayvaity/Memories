package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryMediaRepository
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.core.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCurrentUserUseCaseTest {

    private val repository = mockk<AppSettingRepository>(relaxed = true)
    private val memoryRepository = mockk<MemoryRepository>(relaxed = true)
    private val memoryMediaRepository = mockk<MemoryMediaRepository>(relaxed = true)
    private val tagRepository = mockk<TagRepository>(relaxed = true)
    private val useCase = UpdateCurrentUserUseCase(
        repository,
        memoryRepository,
        memoryMediaRepository,
        tagRepository
    )

    @Test
    fun invoke_updatesCurrentUserOnRepository() = runTest {
        useCase("firebase-uid")

        coVerify { repository.updateCurrentUser("firebase-uid") }
    }

    @Test
    fun invoke_updatesOwnerOnMemoryMediaAndTags() = runTest {
        useCase("firebase-uid")

        coVerify { memoryRepository.updateOwner("firebase-uid") }
        coVerify { memoryMediaRepository.updateOwner("firebase-uid") }
        coVerify { tagRepository.updateOwner("firebase-uid") }
    }

    @Test
    fun invoke_canResetToLocal() = runTest {
        useCase("local")

        coVerify { repository.updateCurrentUser("local") }
    }

    @Test
    fun invoke_returnsSuccessWhenAllUpdatesSucceed() = runTest {
        val result = useCase("firebase-uid")

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun invoke_returnsErrorWhenAnUpdateFails() = runTest {
        val failure = IllegalStateException("db closed")
        coEvery { memoryRepository.updateOwner(any()) } throws failure

        val result = useCase("firebase-uid")

        assertTrue(result is Result.Error)
        assertEquals(failure, (result as Result.Error).error)
    }
}
