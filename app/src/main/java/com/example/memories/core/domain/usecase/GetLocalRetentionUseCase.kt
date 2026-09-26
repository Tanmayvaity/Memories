package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.domain.repository.AppSettingRepository
import kotlinx.coroutines.flow.Flow

class GetLocalRetentionUseCase(
    private val appSettingRepository: AppSettingRepository,
) {
    operator fun invoke(): Flow<LocalRetention> = appSettingRepository.localRetention
}
