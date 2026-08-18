package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.feature.feature_firebase.domain.model.SignUpResult
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository

class SignInWithEmailAndPasswordUseCase(
    private val remoteSyncRepository: RemoteSyncRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): SignUpResult {
        return remoteSyncRepository.signInWithEmailAndPassword(
            email, password
        )
    }
}