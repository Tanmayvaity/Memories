package com.example.memories.feature.feature_feed.domain.usecase

import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow

class ObserveMediaChangesUseCase(
    val repository: MediaFeedRepository
) {
    suspend operator fun invoke(): Flow<Unit>{
        return repository.observeChanges()
    }

}