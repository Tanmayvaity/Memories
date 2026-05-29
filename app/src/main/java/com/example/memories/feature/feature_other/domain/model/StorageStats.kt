package com.example.memories.feature.feature_other.domain.model


data class StorageStats(
    val appBytes : Long,
    val dataBytes : Long,
    val cacheBytes : Long,
    val total : Long
)
