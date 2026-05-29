package com.example.memories.feature.feature_other.domain.usecase

data class SystemUseCaseWrapper(
    val getStorageStatsUseCase: GetStorageStatsUseCase,
    val deleteCacheUseCase : DeleteCacheUseCase
)
