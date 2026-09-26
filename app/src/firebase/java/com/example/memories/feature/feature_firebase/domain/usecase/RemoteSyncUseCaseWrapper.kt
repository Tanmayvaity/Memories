package com.example.memories.feature.feature_firebase.domain.usecase

import com.example.memories.core.domain.usecase.SetSyncOverCellularUseCase
import com.example.memories.core.domain.usecase.SetSyncHiddenMemoriesUseCase
import com.example.memories.core.domain.usecase.SetLocalRetentionUseCase
import com.example.memories.core.domain.usecase.UpdateCurrentUserUseCase

data class RemoteSyncUseCaseWrapper(
    val createUserWithEmailAndPasswordUseCase: CreateUserWithEmailAndPasswordUseCase,
    val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val signOutUseCase: SignOutUseCase,
    val updateCurrentUserUseCase: UpdateCurrentUserUseCase,
    val setLocalRetentionUseCase: SetLocalRetentionUseCase,
    val setSyncOverCellularUseCase: SetSyncOverCellularUseCase,
    val setSyncHiddenMemoriesUseCase: SetSyncHiddenMemoriesUseCase,
)
