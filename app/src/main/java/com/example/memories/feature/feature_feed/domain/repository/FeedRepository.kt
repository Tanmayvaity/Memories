package com.example.memories.feature.feature_feed.domain.repository

import com.example.memories.core.domain.model.MemoryWithMediaModel


interface FeedRepository{
    suspend fun getMemories(): List<MemoryWithMediaModel>
}