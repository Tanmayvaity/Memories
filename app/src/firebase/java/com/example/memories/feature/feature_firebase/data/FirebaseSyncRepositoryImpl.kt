package com.example.memories.feature.feature_firebase.data

import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.model.SignUpResult
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository

class FirebaseSyncRepositoryImpl(
    private val firebaseManager: FirebaseManager
) : RemoteSyncRepository {
    override val isUserLoggedIn: Boolean
        get() = firebaseManager.isUserLoggedIn

    override fun getCurrentUser(): FirebaseUserData? = firebaseManager.getCurrentUser()

    override fun signOut() {
        firebaseManager.signOut()
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult {
        return firebaseManager.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult {
        return firebaseManager.signInWithEmailAndPassword(email, password)
    }
}