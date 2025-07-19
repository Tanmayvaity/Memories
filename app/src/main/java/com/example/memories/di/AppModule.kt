package com.example.memories.di

import android.content.Context
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.feature.feature_camera.data.data_source.CameraManager
import com.example.memories.feature.feature_camera.data.repository.CameraRepositoryImpl
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import com.example.memories.feature.feature_camera.domain.usecase.BindToCameraUseCase
import com.example.memories.feature.feature_camera.domain.usecase.CameraUseCases
import com.example.memories.feature.feature_camera.domain.usecase.CancelRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.PauseRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.ResumeRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.SetAspectRatioUseCase
import com.example.memories.feature.feature_camera.domain.usecase.SetSurfaceCallbackUseCase
import com.example.memories.feature.feature_camera.domain.usecase.StopRecordingUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TakeMediaUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TapToFocusUseCase
import com.example.memories.feature.feature_camera.domain.usecase.TorchToggleUseCase
import com.example.memories.feature.feature_camera.domain.usecase.ZoomUseCase
import com.example.memories.feature.feature_feed.data.repository.MediaFeedRepositoryImpl
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import com.example.memories.feature.feature_feed.domain.usecaase.DeleteMediaUseCase
import com.example.memories.feature.feature_feed.domain.usecaase.DeleteMediasUseCase
import com.example.memories.feature.feature_feed.domain.usecaase.FeedUseCases
import com.example.memories.feature.feature_feed.domain.usecaase.FetchMediaFromSharedUseCase
import com.example.memories.feature.feature_feed.domain.usecaase.GetMediaThumbnailUseCase
import com.example.memories.feature.feature_feed.domain.usecaase.ObserveMediaChangesUseCase
import com.example.memories.feature.feature_feed.domain.usecaase.SharedUriToInternalUriUseCase
import com.example.memories.feature.feature_media_edit.data.repository.MediaRepositoryImpl
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadVideoUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadWithBitmap
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import com.example.memories.feature.feature_media_edit.domain.usecase.SaveBitmapToInternalStorageUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.UriToBitmapUseCase
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
        @ApplicationContext context : Context
    ) = CameraManager(context)

    @Provides
    @Singleton
    fun provideCameraRepository(cameraManager: CameraManager) : CameraRepository {
        return CameraRepositoryImpl(cameraManager)
    }

    @Provides
    @Singleton
    fun provideUseCase(repository: CameraRepository) : CameraUseCases{
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
            CancelRecordingUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideMediaManager(
        @ApplicationContext context : Context
    ) = MediaManager(context)

    @Provides
    @Singleton
    fun provideMediaRepository(mediaManager : MediaManager) : MediaRepository{
        return MediaRepositoryImpl(mediaManager)
    }

   
    @Provides
    @Singleton
    fun provideMediaUseCase(repository: MediaRepository): MediaUseCases{
        return MediaUseCases(
            uriToBitmapUseCase = UriToBitmapUseCase(repository),
            downloadWithBitmap = DownloadWithBitmap(repository),
            saveBitmapToInternalStorageUseCase = SaveBitmapToInternalStorageUseCase(repository),
            downloadVideoUseCase = DownloadVideoUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideMediaFeedRepository(mediaManager: MediaManager): MediaFeedRepository{
        return MediaFeedRepositoryImpl(mediaManager)
    }

    @Provides
    @Singleton
    fun provideFeedMediaUseCase(repository: MediaFeedRepository): FeedUseCases{
        return FeedUseCases(
            fetchMediaFromSharedUseCase = FetchMediaFromSharedUseCase(repository),
            deleteMediaUseCase = DeleteMediaUseCase(repository),
            deleteMediasUseCase = DeleteMediasUseCase(repository),
            sharedUriToInternalUriUseCase = SharedUriToInternalUriUseCase(repository),
            observeMediaChangesUseCase = ObserveMediaChangesUseCase(repository),
            getMediaThumbnailUseCase = GetMediaThumbnailUseCase(repository)
        )
    }


}