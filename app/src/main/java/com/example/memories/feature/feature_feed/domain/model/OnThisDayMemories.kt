package com.example.memories.feature.feature_feed.domain.model

import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.domain.model.MemoryWithMediaModel

data class OnThisDayMemories(
    val label: String,
    val memories: List<MemoryWithMediaModel>
)
