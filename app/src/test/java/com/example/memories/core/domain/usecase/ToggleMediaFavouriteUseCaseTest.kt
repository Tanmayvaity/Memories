package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.MemoryRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleMediaFavouriteUseCaseTest {

    private val repository = mockk<MemoryRepository>(relaxed = true)
    private val useCase = ToggleMediaFavouriteUseCase(repository)

    @Test
    fun invoke_flipsFavouriteToFalseWhenCurrentlyTrue() = runTest {
        useCase(mediaId = "med1", currentFavouriteState = true)

        coVerify { repository.updateMediaFavouriteState("med1", false) }
    }

    @Test
    fun invoke_flipsFavouriteToTrueWhenCurrentlyFalse() = runTest {
        useCase(mediaId = "med1", currentFavouriteState = false)

        coVerify { repository.updateMediaFavouriteState("med1", true) }
    }
}
