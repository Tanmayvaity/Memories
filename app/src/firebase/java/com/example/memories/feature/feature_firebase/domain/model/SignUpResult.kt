package com.example.memories.feature.feature_firebase.domain.model

import com.google.firebase.auth.FirebaseUser

sealed interface SignUpResult {
    data class Success(val user: FirebaseUser) : SignUpResult
    data object EmailAlreadyInUse : SignUpResult
    data object WeakPassword : SignUpResult
    data object InvalidEmail : SignUpResult
    data object NoNetwork : SignUpResult
    data class Unknown(val cause: Throwable) : SignUpResult
}