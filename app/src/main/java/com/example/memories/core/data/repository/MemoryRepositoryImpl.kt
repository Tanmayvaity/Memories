package com.example.memories.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.Query
import androidx.room.Transaction
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemoryRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
    val memoryDao: MemoryDao,
    val tagDao: TagDao,
) : MemoryRepository {
    companion object{
        private const val TAG = "MemoryRepositoryImpl"
    }

    override suspend fun saveToInternalStorage(uriList: List<Uri>): Result<List<Uri>> {
        return mediaManager.saveToInternalStorage(uriList)
    }

    override suspend fun insertMemory(memory: MemoryModel) {
        memoryDao.insertMemory(memory.toEntity())
    }

    override suspend fun updateMemory(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList: List<TagModel>
    ) {
        memoryDao.updateMemory(
            memory.toEntity(),
            mediaList.map { it -> it.toEntity() },
            tagList.map { it -> it.toEntity() })
    }


    override suspend fun insertMedia(mediaList: List<MediaModel>) {
        memoryDao.insertAllMedia(mediaList.map { media -> media.toEntity() })
    }

    override suspend fun insertMemoryWithMediaAndTag(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList: List<TagModel>,
    ) {
        memoryDao.insertMemoryWithMediaAndTag(
            memory.toEntity(),
            mediaList.map { media -> media.toEntity() },
            tagList = tagList.map { tag -> tag.toEntity() })
    }

    override suspend fun insertMemoryTagCrossRef(refs: List<MemoryTagCrossRefModel>) {
        memoryDao.insertMemoryTagCrossRef(refs.map { it -> it.toEntity() })
    }

    override fun getMemories(
        type: FetchType,
        sortType: SortType,
        orderByType: SortOrder
    ): Flow<PagingData<MemoryWithMediaModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
//                Log.d(TAG, "Creating new PagingSource")
                when (type) {
                    FetchType.ALL -> {
                        when (sortType) {
                            SortType.DateAdded -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllMemoriesWithMedia()
                                SortOrder.Ascending -> memoryDao.getAllMemoriesWithMediaAscending()
                            }

                            SortType.CreatedForDate -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllMemoriesWithMediaByMemoryForTimeStamp()
                                SortOrder.Ascending -> memoryDao.getAllMemoriesWithMediaByMemoryForTimeStampAscending()
                            }

                            SortType.Title -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllMemoriesWithMediaByTitle()
                                SortOrder.Ascending -> memoryDao.getAllMemoriesWithMediaByTitleAscending()
                            }
                        }
                    }

                    FetchType.FAVORITE -> {
                        when (sortType) {
                            SortType.DateAdded -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllFavouriteMemoriesWithMedia()
                                SortOrder.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaAscending()
                            }

                            SortType.CreatedForDate -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStamp()
                                SortOrder.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStampAscending()
                            }

                            SortType.Title -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllFavouriteMemoriesWithMediaByTitle()
                                SortOrder.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaByTitleAscending()
                            }
                        }
                    }

                    FetchType.HIDDEN -> {
                        when (sortType) {
                            SortType.DateAdded -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllHiddenMemoriesWithMedia()
                                SortOrder.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaAscending()
                            }

                            SortType.CreatedForDate -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllHiddenMemoriesWithMediaByMemoryForTimeStamp()
                                SortOrder.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaByMemoryForTimeStampAscending()
                            }

                            SortType.Title -> when (orderByType) {
                                SortOrder.Descending -> memoryDao.getAllHiddenMemoriesWithMediaByTitle()
                                SortOrder.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaByTitleAscending()
                            }
                        }
                    }
                }
            }
        ).flow.map { pagingData ->
            pagingData.map {
//                Log.d(TAG, "Paging : loading items ${it.memory.memoryId}")
                it.toDomain()
            }
        }
    }


    override suspend fun updateFavouriteState(id: String, isFavourite: Boolean) {
        return memoryDao.updateFavourite(id, isFavourite)
    }

    override suspend fun updateHiddenState(id: String, isHidden: Boolean) {
        return memoryDao.updateHidden(id, isHidden)
    }

    override suspend fun getMemoryById(id: String): MemoryWithMediaModel? {
        return memoryDao.getMemoryById(id)?.toDomain()
    }

    override suspend fun delete(memory: MemoryModel): Int {
        return memoryDao.deleteMemory(memory.toEntity())
    }

    override suspend fun getMemoryByTitle(query: String): Flow<List<MemoryWithMediaModel>> {
        return memoryDao.getAllMemoriesWithMediaBySearch(query).map { memoryList ->
            memoryList.map { it -> it.toDomain() }
        }
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    override suspend fun getMemoryByTag(id: String): Flow<List<MemoryWithMediaModel>> {
//        return tagDao.getMemoryByTag(id).flatMapLatest { tagWithMemories ->
//            val ids = tagWithMemories.memories.map { it.memoryId }
//            memoryDao.getAllMemoriesWithMediaByTag(ids).map { it ->
//                it.map { it -> it.toDomain() }
//
//            }
//
//        }
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMemoryByTag(id: String): Flow<PagingData<MemoryWithMediaModel>> {
        return tagDao.getMemoryByTag(id).flatMapLatest { tagWithMemories ->
            val ids = tagWithMemories.memories.map { it.memoryId }
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    Log.d(TAG, "Creating new PagingSource for tag memory")
                    memoryDao.getAllMemoriesWithMediaByTag(ids)
                }
            ).flow.map { pagingData ->
                pagingData.map {
                    Log.d(TAG, "Paging : loading items tag memory ${it.memory.memoryId}")
                    it.toDomain()
                }
            }
        }
    }

    override suspend fun getEarliestMemoryTimeStamp(): Long? {
        return memoryDao.getEarliestMemoryTimestamp()
    }

    override suspend fun getMemoriesWithinRange(min: Long, max: Long): List<MemoryWithMediaModel> {
        return memoryDao.getMemoriesBetweenTimestamps(min, max).map { it -> it.toDomain() }
    }


}