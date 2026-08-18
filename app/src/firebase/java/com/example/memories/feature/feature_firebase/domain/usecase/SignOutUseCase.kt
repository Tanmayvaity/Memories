package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository

class SignOutUseCase(
    private val remoteSyncRepository: RemoteSyncRepository,
) {
    operator fun invoke() {
        remoteSyncRepository.signOut()
    }
}
