package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository

class SetSyncHiddenMemoriesUseCase(
    private val appSettingRepository: AppSettingRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        appSettingRepository.setSyncHiddenMemories(enabled)
    }
}
