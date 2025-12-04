package com.example.memories.feature.feature_memory.domain.repository

import android.net.Uri
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {

    suspend fun saveToInternalStorage(uriList : List<Uri>): Result<List<Uri>>

    suspend fun insertMemory(memory : MemoryModel)

    suspend fun insertMedia(mediaList : List<MediaModel>)

    suspend fun insertMemoryWithMediaAndTag(memory: MemoryModel, mediaList: List<MediaModel>,tagList : List<TagModel>)

    suspend fun insertTags(tags : List<TagModel>)

    suspend fun insertTag(tag : TagModel)

    suspend fun insertMemoryTagCrossRef(refs : List<MemoryTagCrossRefModel>)

    suspend fun fetchTags(): Flow<List<TagModel>>

    suspend fun fetchTagsByLabel(label : String) : Flow<List<TagModel>>


}