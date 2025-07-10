package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.feature.feature_feed.domain.model.MediaImage
import kotlinx.coroutines.flow.Flow

interface MediaFeedRepository {
    suspend fun fetchMediaFromShared(): Flow<MediaImage>
}