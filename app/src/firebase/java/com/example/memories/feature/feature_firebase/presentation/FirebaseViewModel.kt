package com.example.memories.feature.feature_firebase.presentation

import kotlinx.coroutines.flow.combine
import com.example.memories.core.domain.repository.AppSettingRepository
import kotlinx.coroutines.flow.launchIn
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.Result
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
    private val appSettingRepository: AppSettingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FirebaseState())
    val state = _state.asStateFlow()

    private val _oneTimeUiEvents = Channel<FirebaseUiEvent>()
    val oneTimeUiEvents = _oneTimeUiEvents.receiveAsFlow()

    init {
        restoreSession()
        combine(
            appSettingRepository.localRetention,
            appSettingRepository.syncOverCellular,
            appSettingRepository.syncHiddenMemories,
        ) { retention, overCellular, hiddenMemories ->
            _state.update {
                it.copy(
                    localRetention = retention,
                    syncOverCellular = overCellular,
                    syncHiddenMemories = hiddenMemories,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: FirebaseEvents) {
        when (event) {
            is FirebaseEvents.SignInEvent -> signIn(event.email, event.password)
            is FirebaseEvents.CreateUserEvent -> createUser(event.email, event.password)
            FirebaseEvents.LogoutEvent -> logout()
            is FirebaseEvents.AuthModeChanged -> {
                _state.update { it.copy(authMode = event.mode) }
            }
            is FirebaseEvents.LocalRetentionChanged -> viewModelScope.launch {
                remoteSyncUseCases.setLocalRetentionUseCase(event.retention)
            }
            is FirebaseEvents.SyncOverCellularChanged -> viewModelScope.launch {
                remoteSyncUseCases.setSyncOverCellularUseCase(event.enabled)
            }
            is FirebaseEvents.SyncHiddenMemoriesChanged -> viewModelScope.launch {
                remoteSyncUseCases.setSyncHiddenMemoriesUseCase(event.enabled)
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
                    when (val update = remoteSyncUseCases.updateCurrentUserUseCase(result.user.uid)) {
                        is Result.Success -> Log.d(TAG, "current user set to ${result.user.uid}; local memories, media and tags claimed")
                        is Result.Error -> Log.e(TAG, "failed to set current user to ${result.user.uid}", update.error)
                    }
                    _state.update {
                        it.copy(userState = UiState.Success(result.user.toUserData()))
                    }
                }
                else -> {
                    Log.w(TAG, "sign in failed: $result", (result as? SignUpResult.Unknown)?.cause)
                    _state.update { it.copy(userState = null) }
                    _oneTimeUiEvents.send(
                        if (result == SignUpResult.InvalidCredentials) FirebaseUiEvent.InvalidCredentials
                        else FirebaseUiEvent.ShowToast(result.toErrorMessage())
                    )
                }
            }
        }
    }

    private fun createUser(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(userState = UiState.Loading) }
            when (val result = remoteSyncUseCases.createUserWithEmailAndPasswordUseCase(email, password)) {
                is SignUpResult.Success -> {
                    when (val update = remoteSyncUseCases.updateCurrentUserUseCase(result.user.uid)) {
                        is Result.Success -> Log.d(TAG, "current user set to ${result.user.uid}; local memories, media and tags claimed")
                        is Result.Error -> Log.e(TAG, "failed to set current user to ${result.user.uid}", update.error)
                    }
                    _state.update {
                        it.copy(userState = UiState.Success(result.user.toUserData()))
                    }
                }
                else -> {
                    Log.w(TAG, "sign up failed: $result", (result as? SignUpResult.Unknown)?.cause)
                    _state.update { it.copy(userState = null) }
                    _oneTimeUiEvents.send(FirebaseUiEvent.ShowToast(result.toErrorMessage()))
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            when (val signOut = remoteSyncUseCases.signOutUseCase()) {
                is Result.Success -> Log.d(TAG, "signed out; current user reset to local")
                is Result.Error -> Log.e(TAG, "sign out failed", signOut.error)
            }
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
        SignUpResult.InvalidEmail -> "Enter a valid email address"
        SignUpResult.InvalidCredentials -> "Incorrect email or password"
        SignUpResult.AccountDisabled -> "This account has been disabled"
        SignUpResult.TooManyRequests -> "Too many attempts. Try again later."
        SignUpResult.NoNetwork -> "No network connection. Try again."
        is SignUpResult.Unknown -> cause.message ?: "Something went wrong. Try again."
        is SignUpResult.Success -> ""
    }

    companion object {
        private const val TAG = "FirebaseViewModel"
    }
}
