package com.example.memories.feature.feature_feed.domain.model

data class TagWithMemoryCountModel(
    val tagId: String,
    val tagLabel: String,
    val memoryCount: Int
)