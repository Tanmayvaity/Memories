package com.example.memories.feature.feature_feed.domain.usecase

import androidx.paging.PagingData
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchMediaFromSharedUseCase @Inject constructor(
    val repository: MediaFeedRepository
) {
    suspend operator fun invoke(): Flow<PagingData<MediaObject>>{
        return repository.fetchMediaFromShared()
    }
}