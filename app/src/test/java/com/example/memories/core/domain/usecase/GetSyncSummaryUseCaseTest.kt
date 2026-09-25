package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.SyncSummary
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetSyncSummaryUseCaseTest {

    private val appSettingRepository = mockk<AppSettingRepository>()
    private val memoryRepository = mockk<MemoryRepository>()
    private val useCase = GetSyncSummaryUseCase(appSettingRepository, memoryRepository)

    @Test
    fun signedOut_emitsNull_andNeverQueriesCount() = runTest {
        every { appSettingRepository.currentUser } returns flowOf(LOCAL_OWNER)

        assertNull(useCase().first())
        verify(exactly = 0) { memoryRepository.getPendingSyncCount(any()) }
    }

    @Test
    fun signedIn_emitsPendingCountForThatUser() = runTest {
        every { appSettingRepository.currentUser } returns flowOf("uid-a")
        every { memoryRepository.getPendingSyncCount("uid-a") } returns flowOf(4)

        assertEquals(SyncSummary(pendingCount = 4, isUploading = false), useCase().first())
    }

    @Test
    fun followsSignInAndSignOut() = runTest {
        val currentUser = MutableStateFlow("uid-a")
        every { appSettingRepository.currentUser } returns currentUser
        every { memoryRepository.getPendingSyncCount("uid-a") } returns flowOf(2)

        val emissions = mutableListOf<SyncSummary?>()
        val collected = launch {
            useCase().take(2).toList(emissions)
        }
        testScheduler.advanceUntilIdle()
        currentUser.value = LOCAL_OWNER
        testScheduler.advanceUntilIdle()
        collected.join()

        assertEquals(listOf(SyncSummary(pendingCount = 2), null), emissions)
    }
}
