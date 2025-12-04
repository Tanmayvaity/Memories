package com.example.memories.feature.feature_memory.data.repository

import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.MemoryTagCrossRefDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MemoryRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
    val memoryDao: MemoryDao,
    val mediaDao : MediaDao,
    val tagDao : TagDao,
    val memoryTagCrossRefDao : MemoryTagCrossRefDao
) : MemoryRepository {
    override suspend fun saveToInternalStorage(uriList: List<Uri>): Result<List<Uri>> {
        return mediaManager.saveToInternalStorage(uriList)
    }

    override suspend fun insertMemory(memory: MemoryModel) {
        memoryDao.insertMemory(memory.toEntity())
    }

    override suspend fun insertMedia(mediaList: List<MediaModel>) {
        memoryDao.insertAllMedia(mediaList.map { media -> media.toEntity() })
    }

    override suspend fun insertMemoryWithMediaAndTag(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList : List<TagModel>
    ) {
        memoryDao.insertMemoryWithMediaAndTag(
            memory.toEntity(),
            mediaList.map { media -> media.toEntity() },
            tagList = tagList.map { tag -> tag.toEntity() } )
    }

    override suspend fun insertTags(tags: List<TagModel>) {
        tagDao.insertTags(tags.map { it -> it.toEntity() })
    }

    override suspend fun insertTag(tag: TagModel) {
        tagDao.insertTag(tag.toEntity())
    }

    override suspend fun insertMemoryTagCrossRef(refs: List<MemoryTagCrossRefModel>) {
        memoryDao.insertMemoryTagCrossRef(refs.map { it -> it.toEntity() })
    }

    override suspend fun fetchTags(): Flow<List<TagModel>> {
        return tagDao.getAllTags().map { tags -> tags.map { tag -> tag.toDomain() } }
    }

    override suspend fun fetchTagsByLabel(label: String): Flow<List<TagModel>> {
        return tagDao.getAllTagsByLabel(label).map { tags -> tags.map { tag -> tag.toDomain() } }
    }

}

