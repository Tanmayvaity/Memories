package com.example.memories.di

import com.example.memories.feature.feature_firebase.data.FirebaseManager
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryMediaRepository
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.core.domain.usecase.UpdateCurrentUserUseCase
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
        appSettingRepository: AppSettingRepository,
        memoryRepository: MemoryRepository,
        memoryMediaRepository: MemoryMediaRepository,
        tagRepository: TagRepository,
    ): RemoteSyncUseCaseWrapper {
        return RemoteSyncUseCaseWrapper(
            createUserWithEmailAndPasswordUseCase = CreateUserWithEmailAndPasswordUseCase(remoteSyncRepository),
            signInWithEmailAndPasswordUseCase = SignInWithEmailAndPasswordUseCase(remoteSyncRepository),
            isUserLoggedInUseCase = IsUserLoggedInUseCase(remoteSyncRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(remoteSyncRepository),
            signOutUseCase = SignOutUseCase(remoteSyncRepository, appSettingRepository),
            updateCurrentUserUseCase = UpdateCurrentUserUseCase(
                appSettingRepository,
                memoryRepository,
                memoryMediaRepository,
                tagRepository
            ),
        )
    }
}
