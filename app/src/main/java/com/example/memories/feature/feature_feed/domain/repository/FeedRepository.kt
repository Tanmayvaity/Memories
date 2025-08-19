package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.core.domain.model.MemoryWithMediaModel


interface FeedRepository{
    suspend fun getMemories(): List<MemoryWithMediaModel>

    suspend fun updateFavouriteState(id : String,isFavourite : Boolean)

    suspend fun updateHiddenState(id : String,isHidden : Boolean)

}