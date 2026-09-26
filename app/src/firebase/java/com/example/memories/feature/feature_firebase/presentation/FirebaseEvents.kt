package com.example.memories.feature.feature_firebase.presentation

import com.example.memories.core.domain.model.LocalRetention

sealed interface FirebaseEvents {
    data class SignInEvent(val email: String, val password: String) : FirebaseEvents
    data class CreateUserEvent(val email: String, val password: String) : FirebaseEvents
    data object LogoutEvent : FirebaseEvents
    data class AuthModeChanged(val mode: AuthMode) : FirebaseEvents
    data class LocalRetentionChanged(val retention: LocalRetention) : FirebaseEvents
    data class SyncOverCellularChanged(val enabled: Boolean) : FirebaseEvents
    data class SyncHiddenMemoriesChanged(val enabled: Boolean) : FirebaseEvents
}
