package com.example.memories.feature.feature_feed.presentation.feed

import com.example.memories.core.domain.model.MemoryWithMediaModel

data class FeedState(
    val memories : List<MemoryWithMediaModel> = emptyList()
)
