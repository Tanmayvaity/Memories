package com.example.memories.core.domain.repository

import com.example.memories.core.domain.model.TagModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun insertTag(tag : TagModel)
    suspend fun fetchTags(): Flow<List<TagModel>>

    suspend fun fetchTagsByLabel(label : String) : Flow<List<TagModel>>
}