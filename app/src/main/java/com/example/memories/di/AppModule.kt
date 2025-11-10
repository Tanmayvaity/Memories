package com.example.memories.di

import android.content.Context
import androidx.room.Room
import com.example.memories.core.data.data_source.CameraSettingsDatastore
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import com.example.memories.core.data.repository.ThemeRepositoryImpl
import com.example.memories.core.domain.repository.ThemeRespository
import com.example.memories.core.domain.usecase.GetThemeUseCase
import com.example.memories.core.domain.usecase.SetThemeUseCase
import com.example.memories.core.domain.usecase.ThemeUseCase
import com.example.memories.feature.feature_other.data.repository.CameraSettingsRepositoryImpl
import com.example.memories.feature.feature_other.domain.repository.CameraSettingsRepository
import com.example.memories.feature.feature_other.domain.usecase.CameraSettingsUseCase
import com.example.memories.feature.feature_camera.data.data_source.CameraManager
import com.example.memories.feature.feature_camera.data.repository.CameraRepositoryImpl
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import com.example.memories.feature.feature_camera.domain.usecase.BindToCameraUseCase
import com.example.memories.feature.feature_camera.domain.usecase.CameraUseCases
import com.example.memories.feature.feature_camera.domain.usecase.CancelRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.GetCameraSettingsUseCase
import com.example.memories.feature.feature_camera.domain.usecase.PauseRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.ResumeRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.SetAspectRatioUseCase
import com.example.memories.feature.feature_camera.domain.usecase.SetSurfaceCallbackUseCase
import com.example.memories.feature.feature_camera.domain.usecase.StopRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TakeMediaUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TapToFocusUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TorchToggleUseCase
import com.example.memories.feature.feature_camera.domain.usecase.ZoomUseCase
import com.example.memories.feature.feature_feed.data.repository.FeedRepositoryImpl
import com.example.memories.feature.feature_feed.data.repository.MediaFeedRepositoryImpl
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import com.example.memories.feature.feature_feed.domain.usecase.DeleteMediaUseCase
import com.example.memories.feature.feature_feed.domain.usecase.DeleteMediasUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.domain.usecase.MediaFeedUseCases
import com.example.memories.feature.feature_feed.domain.usecase.FetchMediaFromSharedUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetFeedUseCase
import com.example.memories.feature.feature_feed.domain.usecase.GetMediaThumbnailUseCase
import com.example.memories.feature.feature_feed.domain.usecase.SharedUriToInternalUriUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.DeleteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetMemoryByIdUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.SearchByTitleUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleFavouriteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleHiddenUseCase
import com.example.memories.feature.feature_media_edit.data.repository.MediaRepositoryImpl
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadVideoUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadWithBitmap
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import com.example.memories.feature.feature_media_edit.domain.usecase.SaveBitmapToInternalStorageUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.UriToBitmapUseCase
import com.example.memories.feature.feature_memory.data.repository.MemoryRepositoryImpl
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import com.example.memories.feature.feature_memory.domain.usecase.MemoryCreateUseCase
import com.example.memories.feature.feature_memory.domain.usecase.MemoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCameraManager(
        @ApplicationContext context: Context
    ) = CameraManager(context)

    @Provides
    @Singleton
    fun provideCameraRepository(
        cameraManager: CameraManager,
        cameraSettingsDatastore: CameraSettingsDatastore
    ): CameraRepository {
        return CameraRepositoryImpl(cameraManager, cameraSettingsDatastore)
    }

    @Provides
    @Singleton
    fun provideUseCase(repository: CameraRepository): CameraUseCases {
        return CameraUseCases(
            SetSurfaceCallbackUseCase(repository),
            BindToCameraUseCase(repository),
            TorchToggleUseCase(repository),
            ZoomUseCase(repository),
            TakeMediaUseCase(repository),
            SetAspectRatioUseCase(repository),
            TapToFocusUseCase(repository),
            ResumeRecordingUseCase(repository),
            PauseRecordingUseCase(repository),
            StopRecordingUseCase(repository),
            CancelRecordingUseCase(repository),
            GetCameraSettingsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideMediaManager(
        @ApplicationContext context: Context
    ) = MediaManager(context)

    @Provides
    @Singleton
    fun provideMediaRepository(mediaManager: MediaManager): MediaRepository {
        return MediaRepositoryImpl(mediaManager)
    }


    @Provides
    @Singleton
    fun provideMediaUseCase(repository: MediaRepository): MediaUseCases {
        return MediaUseCases(
            uriToBitmapUseCase = UriToBitmapUseCase(repository),
            downloadWithBitmap = DownloadWithBitmap(repository),
            saveBitmapToInternalStorageUseCase = SaveBitmapToInternalStorageUseCase(repository),
            downloadVideoUseCase = DownloadVideoUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideMediaFeedRepository(mediaManager: MediaManager): MediaFeedRepository {
        return MediaFeedRepositoryImpl(mediaManager)
    }

    @Provides
    @Singleton
    fun provideFeedMediaUseCase(repository: MediaFeedRepository): MediaFeedUseCases {
        return MediaFeedUseCases(
            fetchMediaFromSharedUseCase = FetchMediaFromSharedUseCase(repository),
            deleteMediaUseCase = DeleteMediaUseCase(repository),
            deleteMediasUseCase = DeleteMediasUseCase(repository),
            sharedUriToInternalUriUseCase = SharedUriToInternalUriUseCase(repository),
            getMediaThumbnailUseCase = GetMediaThumbnailUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun providesCameraSettingsDatastore(
        @ApplicationContext context: Context
    ): CameraSettingsDatastore {
        return CameraSettingsDatastore(context)
    }

    @Provides
    @Singleton
    fun provideCameraSettingsRepository(cameraSettingsDatastore: CameraSettingsDatastore): CameraSettingsRepository {
        return CameraSettingsRepositoryImpl(cameraSettingsDatastore)
    }

    @Provides
    @Singleton
    fun providesCameraSettingsUseCase(repository: CameraSettingsRepository): CameraSettingsUseCase {
        return CameraSettingsUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesOtherSettingsDatastore(
        @ApplicationContext context: Context
    ): OtherSettingsDatastore {
        return OtherSettingsDatastore(context)
    }

    @Provides
    @Singleton
    fun providesThemeRepository(
        otherSettingsDatastore: OtherSettingsDatastore
    ): ThemeRespository {
        return ThemeRepositoryImpl(otherSettingsDatastore)
    }

    @Provides
    @Singleton
    fun providesThemeUseCase(repository: ThemeRespository): ThemeUseCase {
        return ThemeUseCase(
            getThemeUseCase = GetThemeUseCase(repository),
            setThemeUseCase = SetThemeUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun providesMemoryDatabase(
        @ApplicationContext context: Context
    ): MemoryDatabase {
        return Room.databaseBuilder(
            context,
            MemoryDatabase::class.java,
            "memory-db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesMemoryDao(
        database: MemoryDatabase
    ): MemoryDao = database.memoryDao


    @Provides
    @Singleton
    fun providesMediaDao(
        database: MemoryDatabase
    ): MediaDao = database.mediaDao

    @Provides
    @Singleton
    fun providesMemoryRepository(
         mediaManager : MediaManager,
         mediaDao: MediaDao,
         memoryDao: MemoryDao
    ): MemoryRepository {
        return MemoryRepositoryImpl(
            mediaManager = mediaManager,
            mediaDao = mediaDao,
            memoryDao = memoryDao
        )
    }

    @Provides
    @Singleton
    fun providesMemoryUseCase(
        memoryRepository: MemoryRepository
    ): MemoryUseCase{
        return MemoryUseCase(
            createMemoryUseCase = MemoryCreateUseCase(memoryRepository)
        )
    }

    @Provides
    @Singleton
    fun providesFeedRepository(
        memoryDao: MemoryDao
    ): FeedRepository{
        return FeedRepositoryImpl(memoryDao)
    }

    @Provides
    @Singleton
    fun providesFeedUseCases(
        repository : FeedRepository
    ): FeedUseCaseWrapper{
        return FeedUseCaseWrapper(
            getFeedUseCase = GetFeedUseCase(repository),
            toggleFavouriteUseCase = ToggleFavouriteUseCase(repository),
            toggleHiddenUseCase = ToggleHiddenUseCase(repository),
            getMemoryByIdUseCase = GetMemoryByIdUseCase(repository),
            deleteMemoryUseCase = DeleteUseCase(repository),
            searchByTitleUseCase = SearchByTitleUseCase(repository)
        )

    }
}