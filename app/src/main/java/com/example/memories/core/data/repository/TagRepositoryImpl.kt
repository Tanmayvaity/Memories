package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.presentation.tags.SortBy
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

    override fun getTagsWithMemoryCount(
        sortOrder: SortOrder,
        sortBy: SortBy,
        query: String
    ): Flow<List<TagWithMemoryCountModel>> {
        if(query.isNotEmpty()) return getTagsWithMemoryCountBySearch(query)
        return when(sortBy){
            SortBy.Count -> {
                when(sortOrder){
                    SortOrder.Ascending -> {
                        tagDao.getTagsWithMemoryCountAscending()
                    }
                    SortOrder.Descending -> {
                        tagDao.getTagsWithMemoryCount()
                    }
                }

            }
            SortBy.Label -> {
                when(sortOrder){
                    SortOrder.Ascending -> {
                        tagDao.getTagsWithMemoryCountByLabelAscending()
                    }
                    SortOrder.Descending -> {
                        tagDao.getTagsWithMemoryCountByLabel()
                    }
                }
            }

        }.map { tags -> tags.map { tag -> tag.toDomain() } }

    }

    override fun getTagsWithMemoryCountBySearch(query: String): Flow<List<TagWithMemoryCountModel>> {
        return tagDao.getTagsWithMemoryCountBySearch(query).map { tags -> tags.map { tag -> tag.toDomain() } }
    }

}