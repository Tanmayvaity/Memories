package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import android.R.attr.type
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.repository.FeedRepository
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class DeleteUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(memory : MemoryModel): Int{
        return repository.delete(memory)
    }

}