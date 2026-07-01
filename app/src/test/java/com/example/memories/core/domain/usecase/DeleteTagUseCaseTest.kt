package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.TagRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteTagUseCaseTest {

    private val repository = mockk<TagRepository>(relaxed = true)
    private val useCase = DeleteTagUseCase(repository)

    @Test
    fun invoke_deletesAndReturnsSuccess() = runTest {
        val result = useCase("t1")

        coVerify { repository.deleteTag("t1") }
        assertTrue(result is Result.Success)
    }

    @Test
    fun invoke_returnsErrorWhenRepositoryThrows() = runTest {
        coEvery { repository.deleteTag("t1") } throws RuntimeException("fk constraint")

        val result = useCase("t1")

        assertTrue(result is Result.Error)
    }
}
