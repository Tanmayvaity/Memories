package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.core.domain.model.MemoryWithMediaModel
import kotlinx.coroutines.flow.Flow


interface FeedRepository{
    suspend fun getMemories(): Flow<List<MemoryWithMediaModel>>

    suspend fun updateFavouriteState(id : String,isFavourite : Boolean)

    suspend fun updateHiddenState(id : String,isHidden : Boolean)


    suspend fun getMemoryById(id : String): MemoryWithMediaModel?

}