package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddTagUseCaseTest {

    private val repository = mockk<TagRepository>(relaxed = true)
    private val useCase = AddTagUseCase(repository)

    @Test
    fun invoke_reusesExistingTagWithoutInserting() = runTest {
        val existing = TagModel(tagId = "t1", label = "work")
        coEvery { repository.getTagByLabel("work") } returns existing

        val result = useCase("work")

        assertTrue(result is Result.Success)
        assertEquals(existing, (result as Result.Success).data)
        coVerify(exactly = 0) { repository.insertTag(any()) }
    }

    @Test
    fun invoke_trimsLabelBeforeLookupAndInsert() = runTest {
        coEvery { repository.getTagByLabel("work") } returns null

        val result = useCase("  work  ")

        coVerify { repository.getTagByLabel("work") }
        coVerify { repository.insertTag(match { it.label == "work" }) }
        assertEquals("work", ((result as Result.Success).data)?.label)
    }

    @Test
    fun invoke_insertsNewTagWhenNoneExists() = runTest {
        coEvery { repository.getTagByLabel("new") } returns null

        val result = useCase("new")

        assertTrue(result is Result.Success)
        coVerify { repository.insertTag(match { it.label == "new" }) }
    }

    @Test
    fun invoke_returnsErrorWhenRepositoryThrows() = runTest {
        coEvery { repository.getTagByLabel(any()) } throws RuntimeException("db down")

        val result = useCase("work")

        assertTrue(result is Result.Error)
    }
}
