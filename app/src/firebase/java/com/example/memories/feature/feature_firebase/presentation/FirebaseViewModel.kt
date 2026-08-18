package com.example.memories.feature.feature_firebase.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.presentation.UiState
import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.model.SignUpResult
import com.example.memories.feature.feature_firebase.domain.usecase.RemoteSyncUseCaseWrapper
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val remoteSyncUseCases: RemoteSyncUseCaseWrapper,
) : ViewModel() {

    private val _state = MutableStateFlow(FirebaseState())
    val state = _state.asStateFlow()

    private val _snackbarEvents = Channel<String>()
    val snackbarEvents = _snackbarEvents.receiveAsFlow()

    init {
        restoreSession()
    }

    fun onEvent(event: FirebaseEvents) {
        when (event) {
            is FirebaseEvents.SignInEvent -> signIn(event.email, event.password)
            is FirebaseEvents.CreateUserEvent -> createUser(event.email, event.password)
            FirebaseEvents.LogoutEvent -> logout()
            is FirebaseEvents.AuthModeChanged -> {
                _state.update { it.copy(authMode = event.mode) }
            }
        }
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val currentUser = remoteSyncUseCases.getCurrentUserUseCase()
            _state.update {
                it.copy(
                    userState = currentUser?.let { user -> UiState.Success(user) }
                )
            }
        }

    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading) }
            when (val result = remoteSyncUseCases.signInWithEmailAndPasswordUseCase(email, password)) {
                is SignUpResult.Success -> {
                    _state.update {
                        it.copy(userState = UiState.Success(result.user.toUserData()))
                    }
                }
                else -> {
                    _state.update { it.copy(userState = null) }
                    _snackbarEvents.send(result.toErrorMessage())
                }
            }
        }
    }

    private fun createUser(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading) }
            when (val result = remoteSyncUseCases.createUserWithEmailAndPasswordUseCase(email, password)) {
                is SignUpResult.Success -> {
                    _state.update {
                        it.copy(userState = UiState.Success(result.user.toUserData()))
                    }
                }
                else -> {
                    _state.update { it.copy(userState = null) }
                    _snackbarEvents.send(result.toErrorMessage())
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            remoteSyncUseCases.signOutUseCase()
            _state.update { it.copy(userState = null) }
        }

    }

    private fun FirebaseUser.toUserData(): FirebaseUserData {
        return FirebaseUserData(
            uid = uid,
            email = email,
            displayName = displayName,
        )
    }

    private fun SignUpResult.toErrorMessage(): String = when (this) {
        SignUpResult.EmailAlreadyInUse -> "This email is already registered"
        SignUpResult.WeakPassword -> "Use at least 6 characters for your password"
        SignUpResult.InvalidEmail -> "Enter a valid email and password"
        SignUpResult.NoNetwork -> "No network connection. Try again."
        is SignUpResult.Unknown -> cause.message ?: "Something went wrong. Try again."
        is SignUpResult.Success -> ""
    }
}
