package com.example.memories.core.domain.repository

import com.example.memories.core.domain.model.TagModel
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun insertTag(tag : TagModel)
    suspend fun fetchTags(): Flow<List<TagModel>>

    suspend fun fetchTagsByLabel(label : String) : Flow<List<TagModel>>

    suspend fun deleteTag(id : String)

    fun getTagsWithMemoryCount() : Flow<List<TagWithMemoryCountModel>>
}