package com.example.memories.feature.feature_other.domain.repository

import com.example.memories.feature.feature_other.domain.model.StorageStats

interface SystemRepository {
    suspend fun getStorageStats() : StorageStats
    suspend fun deleteCache() : Result<Long>
}