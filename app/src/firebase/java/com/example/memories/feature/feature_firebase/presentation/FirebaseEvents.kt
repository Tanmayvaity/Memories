package com.example.memories.feature.feature_firebase.presentation

sealed interface FirebaseEvents {
    data class SignInEvent(val email: String, val password: String) : FirebaseEvents
    data class CreateUserEvent(val email: String, val password: String) : FirebaseEvents
    data object LogoutEvent : FirebaseEvents
    data class AuthModeChanged(val mode: AuthMode) : FirebaseEvents
}
