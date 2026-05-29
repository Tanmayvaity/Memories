package com.example.memories.feature.feature_other.domain.usecase

import com.example.memories.feature.feature_other.domain.model.StorageStats
import com.example.memories.feature.feature_other.domain.repository.SystemRepository

class DeleteCacheUseCase(
    private val systemRepository: SystemRepository
) {
    suspend operator fun invoke(): Result<Long> {
        return systemRepository.deleteCache()
    }
}
