package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository

class GetCurrentUserUseCase(
    private val remoteSyncRepository: RemoteSyncRepository,
) {
    operator fun invoke(): FirebaseUserData? = remoteSyncRepository.getCurrentUser()
}
