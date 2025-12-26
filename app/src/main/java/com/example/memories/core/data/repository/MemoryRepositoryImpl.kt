package com.example.memories.core.data.repository

import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
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
import com.example.memories.feature.feature_feed.domain.model.OrderByType
import com.example.memories.feature.feature_feed.domain.model.SortType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemoryRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
    val memoryDao: MemoryDao,
    val tagDao : TagDao,
) : MemoryRepository {
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
        memoryDao.updateMemory(memory.toEntity(),mediaList.map { it -> it.toEntity() },tagList.map { it -> it.toEntity()})
    }


    override suspend fun insertMedia(mediaList: List<MediaModel>) {
        memoryDao.insertAllMedia(mediaList.map { media -> media.toEntity() })
    }

    override suspend fun insertMemoryWithMediaAndTag(
        memory: MemoryModel,
        mediaList: List<MediaModel>,
        tagList : List<TagModel>,
    ) {
        memoryDao.insertMemoryWithMediaAndTag(
            memory.toEntity(),
            mediaList.map { media -> media.toEntity() },
            tagList = tagList.map { tag -> tag.toEntity() } )
    }

    override suspend fun insertMemoryTagCrossRef(refs: List<MemoryTagCrossRefModel>) {
        memoryDao.insertMemoryTagCrossRef(refs.map { it -> it.toEntity() })
    }

    override suspend fun getMemories(type: FetchType, sortType: SortType, orderByType: OrderByType): Flow<List<MemoryWithMediaModel>> {
        return when (type) {
            FetchType.ALL -> {
                when(sortType){
                    SortType.DateAdded -> {
                        when(orderByType){
                            OrderByType.Descending -> memoryDao.getAllMemoriesWithMedia()
                            OrderByType.Ascending -> memoryDao.getAllMemoriesWithMediaAscending()
                        }
                    }
                    SortType.CreatedForDate -> {
                        when(orderByType){
                            OrderByType.Descending -> memoryDao.getAllMemoriesWithMediaByMemoryForTimeStamp()
                            OrderByType.Ascending -> memoryDao.getAllMemoriesWithMediaByMemoryForTimeStampAscending()
                        }
                    }
                    SortType.Title -> {
                        when(orderByType){
                            OrderByType.Descending -> memoryDao.getAllMemoriesWithMediaByTitle()
                            OrderByType.Ascending -> memoryDao.getAllMemoriesWithMediaByTitleAscending()
                        }

                    }

                }
            }
            FetchType.FAVORITE -> {
                when(sortType){
                    SortType.CreatedForDate -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStampAscending()
                            OrderByType.Descending -> memoryDao.getAllFavouriteMemoriesWithMediaByMemoryForTimeStamp()
                        }
                    }
                    SortType.DateAdded -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaAscending()
                            OrderByType.Descending -> memoryDao.getAllFavouriteMemoriesWithMedia()
                        }
                    }
                    SortType.Title -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllFavouriteMemoriesWithMediaByTitleAscending()
                            OrderByType.Descending -> memoryDao.getAllFavouriteMemoriesWithMediaByTitle()
                        }
                    }

                }
            }
            FetchType.HIDDEN -> {
                when(sortType){
                    SortType.CreatedForDate -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaByMemoryForTimeStampAscending()
                            OrderByType.Descending -> memoryDao.getAllHiddenMemoriesWithMediaByMemoryForTimeStamp()
                        }
                    }
                    SortType.DateAdded -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaAscending()
                            OrderByType.Descending -> memoryDao.getAllHiddenMemoriesWithMedia()
                        }
                    }
                    SortType.Title -> {
                        when(orderByType){
                            OrderByType.Ascending -> memoryDao.getAllHiddenMemoriesWithMediaByTitleAscending()
                            OrderByType.Descending -> memoryDao.getAllHiddenMemoriesWithMediaByTitle()
                        }
                    }
                }
            }
        }.map { memoryList -> memoryList.map { it -> it.toDomain() } }
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
        return memoryDao.getAllMemoriesWithMediaByTitle(query).map { memoryList ->
            memoryList.map { it -> it.toDomain() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMemoryByTag(id: String): Flow<List<MemoryWithMediaModel>> {
        return tagDao.getMemoryByTag(id).flatMapLatest { tagWithMemories ->
            val ids = tagWithMemories.memories.map { it.memoryId }
            memoryDao.getAllMemoriesWithMediaByTag(ids).map { it ->
                it.map { it -> it.toDomain() }

            }

        }
    }


}