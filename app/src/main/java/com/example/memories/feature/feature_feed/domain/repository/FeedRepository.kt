package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import kotlinx.coroutines.flow.Flow


interface FeedRepository{
    suspend fun getMemories(type : FetchType): Flow<List<MemoryWithMediaModel>>

    suspend fun updateFavouriteState(id : String,isFavourite : Boolean)

    suspend fun updateHiddenState(id : String,isHidden : Boolean)


    suspend fun getMemoryById(id : String): MemoryWithMediaModel?

//    suspend fun deleteById(id : String)

    suspend fun delete(memory : MemoryModel) : Int

}