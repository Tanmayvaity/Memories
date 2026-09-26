package com.example.memories.feature.feature_firebase.presentation

import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.presentation.UiState
import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData

data class FirebaseState(
    val authMode: AuthMode = AuthMode.LOGIN,
    val userState: UiState<FirebaseUserData>? = null,
    val localRetention: LocalRetention = LocalRetention.Default,
) {
    val isSignedIn: Boolean
        get() = userState is UiState.Success

    val isAuthLoading: Boolean
        get() = userState is UiState.Loading
}
