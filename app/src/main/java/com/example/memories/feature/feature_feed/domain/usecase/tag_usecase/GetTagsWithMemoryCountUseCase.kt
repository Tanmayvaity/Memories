package com.example.memories.feature.feature_feed.domain.usecase.tag_usecase

import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import kotlinx.coroutines.flow.Flow

class GetTagsWithMemoryCountUseCase(
    private val repository : TagRepository
) {
    operator fun invoke(): Flow<List<TagWithMemoryCountModel>> {
        return repository.getTagsWithMemoryCount()
    }
}