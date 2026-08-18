package com.example.memories.feature.feature_firebase.domain.repository

import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.model.SignUpResult

interface RemoteSyncRepository {
    val isUserLoggedIn: Boolean

    fun getCurrentUser(): FirebaseUserData?

    fun signOut()

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): SignUpResult
}