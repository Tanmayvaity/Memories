package com.example.memories.feature.feature_firebase.domain.usecase

import android.util.Log
import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository
import kotlin.coroutines.cancellation.CancellationException

/**
 * Rows keep the signed-out user's id as owner, so the next account to sign in cannot claim them.
 */
class SignOutUseCase(
    private val remoteSyncRepository: RemoteSyncRepository,
    private val appSettingRepository: AppSettingRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return runCatching {
            remoteSyncRepository.signOut()
            appSettingRepository.updateCurrentUser(LOCAL_OWNER)
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { e ->
                if (e is CancellationException) throw e
                Log.e(TAG, "error while signing out error : ${e.message}")
                Result.Error(e)
            }
        )
    }

    companion object {
        private const val TAG = "SignOutUseCase"
    }
}
