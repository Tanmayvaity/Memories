package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository

class IsUserLoggedInUseCase(
    private val remoteSyncRepository: RemoteSyncRepository,
) {
    operator fun invoke(): Boolean = remoteSyncRepository.isUserLoggedIn
}
