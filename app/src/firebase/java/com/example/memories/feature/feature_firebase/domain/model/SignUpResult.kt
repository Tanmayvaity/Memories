package com.example.memories.feature.feature_firebase.domain.model

import com.google.firebase.auth.FirebaseUser

sealed interface SignUpResult {
    data class Success(val user: FirebaseUser) : SignUpResult
    data object EmailAlreadyInUse : SignUpResult
    data object WeakPassword : SignUpResult
    /** Email is blank or badly formatted. */
    data object InvalidEmail : SignUpResult
    /** Sign-in: wrong password or no such account (Firebase won't say which). */
    data object InvalidCredentials : SignUpResult
    data object AccountDisabled : SignUpResult
    data object TooManyRequests : SignUpResult
    data object NoNetwork : SignUpResult
    data class Unknown(val cause: Throwable) : SignUpResult
}