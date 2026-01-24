package com.example.memories.core.domain.repository

import android.net.Uri
import androidx.paging.PagingData
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {

    suspend fun saveToInternalStorage(uriList: List<Uri>): Result<List<Uri>>

    suspend fun insertMemory(memory: MemoryModel)
    suspend fun updateMemory(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList: List<TagModel>
    )

    suspend fun insertMedia(mediaList: List<MediaModel>)

    suspend fun insertMemoryWithMediaAndTag(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList: List<TagModel>
    )

    suspend fun insertMemoryTagCrossRef(refs: List<MemoryTagCrossRefModel>)

    fun getMemories(
        type: FetchType,
        sortType: SortType,
        orderByType: SortOrder
    ): Flow<PagingData<MemoryWithMediaModel>>

    suspend fun updateFavouriteState(id: String, isFavourite: Boolean)

    suspend fun updateHiddenState(id: String, isHidden: Boolean)


    suspend fun getMemoryById(id: String): MemoryWithMediaModel?

    suspend fun delete(memory: MemoryModel): Int

    suspend fun getMemoryByTitle(query: String): Flow<List<MemoryWithMediaModel>>


     fun getMemoryByTag(id: String): Flow<PagingData<MemoryWithMediaModel>>

    suspend fun getEarliestMemoryTimeStamp(): Long?

    suspend fun getMemoriesWithinRange(
        min: Long,
        max: Long,
    ): Flow<List<MemoryWithMediaModel>>

    suspend fun getRecentMemories(limit : Int) : Flow<List<MemoryWithMediaModel>>
}