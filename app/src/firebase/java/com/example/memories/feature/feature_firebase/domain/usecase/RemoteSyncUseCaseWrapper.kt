package com.example.memories.feature.feature_firebase.domain.usecase

data class RemoteSyncUseCaseWrapper(
    val createUserWithEmailAndPasswordUseCase: CreateUserWithEmailAndPasswordUseCase,
    val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val signOutUseCase: SignOutUseCase,
)
