package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class SignOutUseCaseTest {

    private val remoteSyncRepository = mockk<RemoteSyncRepository>(relaxed = true)
    private val appSettingRepository = mockk<AppSettingRepository>(relaxed = true)
    private val useCase = SignOutUseCase(remoteSyncRepository, appSettingRepository)

    @Test
    fun invoke_signsOutThenResetsCurrentUserToLocal() = runTest {
        val result = useCase()

        assertEquals(Result.Success(Unit), result)
        coVerifyOrder {
            remoteSyncRepository.signOut()
            appSettingRepository.updateCurrentUser(LOCAL_OWNER)
        }
    }

    @Test
    fun invoke_whenSignOutFails_returnsErrorAndKeepsCurrentUser() = runTest {
        val failure = IllegalStateException("auth unavailable")
        every { remoteSyncRepository.signOut() } throws failure

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(failure, (result as Result.Error).error)
        coVerify(exactly = 0) { appSettingRepository.updateCurrentUser(any()) }
    }

    @Test
    fun invoke_whenDataStoreWriteFails_returnsError() = runTest {
        val failure = IllegalStateException("disk full")
        coEvery { appSettingRepository.updateCurrentUser(any()) } throws failure

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(failure, (result as Result.Error).error)
    }

    @Test(expected = CancellationException::class)
    fun invoke_rethrowsCancellation() = runTest {
        coEvery { appSettingRepository.updateCurrentUser(any()) } throws CancellationException("cancelled")

        useCase()
    }
}
