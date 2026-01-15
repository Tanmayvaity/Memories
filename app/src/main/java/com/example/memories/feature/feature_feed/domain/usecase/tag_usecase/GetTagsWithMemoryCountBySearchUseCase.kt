package com.example.memories.feature.feature_feed.domain.usecase.tag_usecase

import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.presentation.tags.SortBy
import kotlinx.coroutines.flow.Flow

class GetTagsWithMemoryCountBySearchUseCase(
    private val repository : TagRepository
) {
    operator fun invoke(
        query : String
    ): Flow<List<TagWithMemoryCountModel>> {
        return repository.getTagsWithMemoryCountBySearch(query)
    }
}