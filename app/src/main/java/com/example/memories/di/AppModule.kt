package com.example.memories.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.WorkManager
import com.example.memories.core.data.data_source.CameraSettingsDatastore
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.MemoryTagCrossRefDao
import com.example.memories.core.data.data_source.room.dao.SearchDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import com.example.memories.core.data.data_source.room.migrations.MEMORY_MIGRATION_1_2
import com.example.memories.core.data.data_source.room.migrations.MEMORY_MIGRATION_2_3
import com.example.memories.core.data.data_source.room.migrations.MEMORY_MIGRATION_3_4
import com.example.memories.core.data.data_source.room.migrations.MEMORY_MIGRATION_4_5
import com.example.memories.core.domain.repository.TagRepository
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
import com.example.memories.feature.feature_feed.data.repository.MediaFeedRepositoryImpl
import com.example.memories.feature.feature_feed.data.repository.RecentSearchRepositoryImpl
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
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
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchRecentSearchUseCase
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.RecentSearchWrapper
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.SaveSearchIdUseCase
import com.example.memories.feature.feature_media_edit.data.repository.MediaRepositoryImpl
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadVideoUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.DownloadWithBitmap
import com.example.memories.feature.feature_media_edit.domain.usecase.MediaUseCases
import com.example.memories.feature.feature_media_edit.domain.usecase.SaveBitmapToInternalStorageUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.UriToBitmapUseCase
import com.example.memories.core.data.repository.MemoryRepositoryImpl
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.core.domain.usecase.AddTagUseCase
import com.example.memories.core.domain.usecase.FetchTagUseCase
import com.example.memories.core.domain.usecase.FetchTagsByLabelUseCase
import com.example.memories.core.data.repository.TagRepositoryImpl
import com.example.memories.core.domain.usecase.DeleteTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.TagWithMemoryUseCaseWrapper
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchMemoryByTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchOnThisDataUseCase
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.GetTagsWithMemoryCountBySearchUseCase
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.GetTagsWithMemoryCountUseCase
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.TagUseCaseWrapper
import com.example.memories.feature.feature_media_edit.domain.usecase.ApplyAdjustFilterUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.ApplyFilterUseCase
import com.example.memories.feature.feature_media_edit.domain.usecase.SaveToCacheStorageWithBitmapUseCase
import com.example.memories.feature.feature_memory.domain.usecase.MemoryCreateUseCase
import com.example.memories.feature.feature_memory.domain.usecase.MemoryUpdateUseCase
import com.example.memories.feature.feature_memory.domain.usecase.MemoryUseCase
import com.example.memories.feature.feature_notifications.data.AlarmManagerService
import com.example.memories.feature.feature_notifications.data.AlarmReceiver
import com.example.memories.feature.feature_notifications.data.MemoryNotificationSchedulerImpl
import com.example.memories.feature.feature_notifications.data.NotificationRepositoryImpl
import com.example.memories.feature.feature_notifications.domain.repository.MemoryNotificationScheduler
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository
import com.example.memories.feature.feature_notifications.domain.usecase.NotificationUseCase
import com.example.memories.feature.feature_notifications.domain.usecase.SetAllNotificationsUseCase
import com.example.memories.feature.feature_notifications.domain.usecase.SetOnThisDayNotificationUseCase
import com.example.memories.feature.feature_notifications.domain.usecase.SetReminderNotificationUseCase
import com.example.memories.feature.feature_notifications.domain.usecase.SetReminderTimeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
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
    fun provideRecentSearchRepository(search: SearchDao): RecentSearchRepository {
        return RecentSearchRepositoryImpl(search)
    }

    @Provides
    @Singleton
    fun provideSearchDao(
        database: MemoryDatabase
    ): SearchDao = database.searchDao


    @Provides
    @Singleton
    fun provideMediaUseCase(repository: MediaRepository): MediaUseCases {
        return MediaUseCases(
            uriToBitmapUseCase = UriToBitmapUseCase(repository),
            downloadWithBitmap = DownloadWithBitmap(repository),
            saveBitmapToInternalStorageUseCase = SaveBitmapToInternalStorageUseCase(repository),
            downloadVideoUseCase = DownloadVideoUseCase(repository),
            applyFilterUseCase = ApplyFilterUseCase(repository),
            applyAdjustFilterUseCase = ApplyAdjustFilterUseCase(repository),
            saveToCacheStorageWithBitmapUseCase = SaveToCacheStorageWithBitmapUseCase(repository)
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
        ).setQueryCallback(
            { sqlQuery, bindArgs ->
                Log.i("MemoryDatabase", "providesMemoryDatabase: ${sqlQuery.toString()}")
            },
            Dispatchers.IO.asExecutor()
        )
            .addMigrations(
                MEMORY_MIGRATION_1_2,
                MEMORY_MIGRATION_2_3,
                MEMORY_MIGRATION_3_4,
                MEMORY_MIGRATION_4_5
            )
            .build()
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
    fun providesTagDao(
        database: MemoryDatabase
    ): TagDao = database.tagDao

    @Provides
    @Singleton
    fun providesMemoryTagCrossRefDao(
        database: MemoryDatabase
    ): MemoryTagCrossRefDao = database.memoryTagCrossRefDao

    @Provides
    @Singleton
    fun providesMemoryRepository(
        mediaManager: MediaManager,
        memoryDao: MemoryDao,
        tagDao: TagDao,
    ): MemoryRepository {
        return MemoryRepositoryImpl(
            mediaManager = mediaManager,
            memoryDao = memoryDao,
            tagDao = tagDao,
        )
    }


    @Provides
    @Singleton
    fun providesTagRepository(
        tagDao: TagDao
    ): TagRepository {
        return TagRepositoryImpl(tagDao)
    }

    @Provides
    @Singleton
    fun providesMemoryUseCase(
        memoryRepository: MemoryRepository,
        tagRepository: TagRepository
    ): MemoryUseCase {
        return MemoryUseCase(
            createMemoryUseCase = MemoryCreateUseCase(memoryRepository),
            fetchTagUseCase = FetchTagUseCase(tagRepository),
            addTagUseCase = AddTagUseCase(tagRepository),
            fetchTagsByLabelUseCase = FetchTagsByLabelUseCase(tagRepository),
            fetchMemoryByIdUseCase = GetMemoryByIdUseCase(memoryRepository),
            updateMemoryUseCase = MemoryUpdateUseCase(memoryRepository),
            tagDeleteTagUseCase = DeleteTagUseCase(tagRepository)
        )
    }

//    @Provides
//    @Singleton
//    fun providesFeedRepository(
//        memoryDao: MemoryDao,
//        tagDao: TagDao
//    ): FeedRepository{
//        return FeedRepositoryImpl(memoryDao,tagDao)
//    }

    @Provides
    @Singleton
    fun providesFeedUseCases(
        repository: MemoryRepository,
        tagRepository: TagRepository,
        mediaRepository: MediaRepository
    ): FeedUseCaseWrapper {
        return FeedUseCaseWrapper(
            getFeedUseCase = GetFeedUseCase(repository),
            toggleFavouriteUseCase = ToggleFavouriteUseCase(repository),
            toggleHiddenUseCase = ToggleHiddenUseCase(repository),
            getMemoryByIdUseCase = GetMemoryByIdUseCase(repository),
            deleteMemoryUseCase = DeleteUseCase(repository, mediaRepository),
            searchByTitleUseCase = SearchByTitleUseCase(repository),
            fetchTagUseCase = FetchTagUseCase(tagRepository),
            fetchMemoryByTagUseCase = FetchMemoryByTagUseCase(repository),
            fetchOnThisDataUseCase = FetchOnThisDataUseCase(repository)
        )

    }

    @Provides
    @Singleton
    fun provideRecentSearchWrapper(
        repository: RecentSearchRepository
    ): RecentSearchWrapper {
        return RecentSearchWrapper(
            saveSearchIdUseCase = SaveSearchIdUseCase(repository),
            fetchRecentSearchUseCase = FetchRecentSearchUseCase(repository)
        )

    }

    @Provides
    @Singleton
    fun provideTagsUseCaseWrapper(
        repository: TagRepository
    ): TagUseCaseWrapper {
        return TagUseCaseWrapper(
            getTagsWithMemoryCountUseCase = GetTagsWithMemoryCountUseCase(repository),
            deleteTagUseCase = DeleteTagUseCase(repository),
            getTagsWithMemoryCountBySearchUseCase = GetTagsWithMemoryCountBySearchUseCase(repository),
            addTagUseCase = AddTagUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideTagWithMemoryUseCaseWrapper(
        tagRepository: TagRepository,
        memoryRepository: MemoryRepository
    ): TagWithMemoryUseCaseWrapper {
        return TagWithMemoryUseCaseWrapper(
            fetchMemoryByTagUseCase = FetchMemoryByTagUseCase(memoryRepository),
            deleteTagUseCase = DeleteTagUseCase(tagRepository)
        )
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        otherSettingsDatastore: OtherSettingsDatastore
    ): NotificationRepository {
        return NotificationRepositoryImpl(otherSettingsDatastore)
    }

    @Provides
    @Singleton
    fun provideNotificationUseCase(
        repository: NotificationRepository,
        scheduler: MemoryNotificationScheduler
    ): NotificationUseCase {
        return NotificationUseCase(
            setAllNotificationsUseCase = SetAllNotificationsUseCase(repository, scheduler),
            setReminderNotificationUseCase = SetReminderNotificationUseCase(repository, scheduler),
            setOnThisDayNotificationUseCase = SetOnThisDayNotificationUseCase(
                repository,
                scheduler
            ),
            setReminderTimeUseCase = SetReminderTimeUseCase(repository, scheduler)
        )

    }

    @Provides
    @Singleton
    fun provideMemoryNotificationScheduler(
        workManager: WorkManager,
        alarmManagerService: AlarmManagerService
    ): MemoryNotificationScheduler {
        return MemoryNotificationSchedulerImpl(
            workManager = workManager,
            alarmManagerService = alarmManagerService
        )

    }

    @Provides
    @Singleton
    fun provideAlarmManagerService(
        @ApplicationContext context: Context
    ): AlarmManagerService{
        return AlarmManagerService(context)
    }

    @Provides
    @Singleton
    fun provideNotificationService(
        @ApplicationContext context: Context
    ): NotificationService {
        return NotificationService(
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context
    ): Context {
        return context;
    }

}