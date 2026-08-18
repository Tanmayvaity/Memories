package com.example.memories.di

import com.example.memories.feature.feature_firebase.data.FirebaseManager
import com.example.memories.feature.feature_firebase.data.FirebaseSyncRepositoryImpl
import com.example.memories.feature.feature_firebase.domain.repository.RemoteSyncRepository
import com.example.memories.feature.feature_firebase.domain.usecase.CreateUserWithEmailAndPasswordUseCase
import com.example.memories.feature.feature_firebase.domain.usecase.GetCurrentUserUseCase
import com.example.memories.feature.feature_firebase.domain.usecase.IsUserLoggedInUseCase
import com.example.memories.feature.feature_firebase.domain.usecase.RemoteSyncUseCaseWrapper
import com.example.memories.feature.feature_firebase.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.example.memories.feature.feature_firebase.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseManager(): FirebaseManager = FirebaseManager()

    @Provides
    @Singleton
    fun provideRemoteSyncRepository(
        firebaseManager: FirebaseManager,
    ): RemoteSyncRepository = FirebaseSyncRepositoryImpl(firebaseManager)

    @Provides
    @Singleton
    fun provideRemoteSyncUseCaseWrapper(
        remoteSyncRepository: RemoteSyncRepository,
    ): RemoteSyncUseCaseWrapper {
        return RemoteSyncUseCaseWrapper(
            createUserWithEmailAndPasswordUseCase = CreateUserWithEmailAndPasswordUseCase(remoteSyncRepository),
            signInWithEmailAndPasswordUseCase = SignInWithEmailAndPasswordUseCase(remoteSyncRepository),
            isUserLoggedInUseCase = IsUserLoggedInUseCase(remoteSyncRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(remoteSyncRepository),
            signOutUseCase = SignOutUseCase(remoteSyncRepository),
        )
    }
}
