package com.example.memories.feature.feature_other.data.repository

import android.content.Context
import com.example.memories.feature.feature_other.data.repository.data_source.SystemManager
import com.example.memories.feature.feature_other.domain.model.StorageStats
import com.example.memories.feature.feature_other.domain.repository.SystemRepository

class SystemRepositoryImpl(
    private val systemManager : SystemManager
): SystemRepository{
    override suspend fun getStorageStats(): StorageStats {
        return systemManager.getStorageStats()
    }

    override suspend fun deleteCache(): Result<Long> {
        return systemManager.deleteCacheData()
    }

}