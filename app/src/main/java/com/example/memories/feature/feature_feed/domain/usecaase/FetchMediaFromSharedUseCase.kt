package com.example.memories.feature.feature_feed.domain.usecaase

import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMediaFromSharedUseCase @Inject constructor(
    val repository: MediaFeedRepository
) {
    suspend operator fun invoke(): Flow<MediaImage>{
        return repository.fetchMediaFromShared()
    }
}