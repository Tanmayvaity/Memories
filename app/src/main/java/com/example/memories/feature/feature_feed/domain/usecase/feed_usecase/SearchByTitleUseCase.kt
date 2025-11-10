package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import android.R.attr.type
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

class SearchByTitleUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(query : String): Flow<List<MemoryWithMediaModel>> {
        return repository.getMemoryByTitle(query)
    }

}