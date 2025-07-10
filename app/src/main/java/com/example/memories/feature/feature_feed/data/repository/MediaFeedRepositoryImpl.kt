package com.example.memories.feature.feature_feed.data.repository

import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MediaFeedRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager
) : MediaFeedRepository {
    override suspend fun fetchMediaFromShared(): Flow<MediaImage> {
        return mediaManager.fetchMediaFromShared()
    }

}