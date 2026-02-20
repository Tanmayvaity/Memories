package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: AppSettingRepository
) {
    operator fun invoke() : Flow<Boolean>{
        return repository.isDarkModeEnabled
    }

}