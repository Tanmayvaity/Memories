package com.example.memories.feature.feature_other.domain.usecase

import com.example.memories.feature.feature_other.domain.model.StorageStats
import com.example.memories.feature.feature_other.domain.repository.SystemRepository

class GetStorageStatsUseCase(
    private val systemRepository: SystemRepository
) {
    suspend operator fun invoke(): StorageStats {
        return systemRepository.getStorageStats()
    }
}
