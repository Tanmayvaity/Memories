package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    val tagDao: TagDao
) : TagRepository {
    override suspend fun insertTag(tag: TagModel) {
        tagDao.insertTag(tag.toEntity())
    }

    override suspend fun fetchTags(): Flow<List<TagModel>> {
        return tagDao.getAllTags().map { tags -> tags.map { tag -> tag.toDomain() } }
    }

    override suspend fun fetchTagsByLabel(label: String): Flow<List<TagModel>> {
        return tagDao.getAllTagsByLabel(label).map { tags -> tags.map { tag -> tag.toDomain() } }
    }

    override suspend fun deleteTag(id: String) {
        tagDao.deleteTag(id)
    }

}