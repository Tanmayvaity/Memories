package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.domain.repository.AppSettingRepository

class SetLocalRetentionUseCase(
    private val appSettingRepository: AppSettingRepository,
) {
    suspend operator fun invoke(retention: LocalRetention) {
        appSettingRepository.setLocalRetention(retention)
    }
}
