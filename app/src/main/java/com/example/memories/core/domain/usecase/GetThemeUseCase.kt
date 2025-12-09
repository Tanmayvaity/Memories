package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.ThemeRespository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: ThemeRespository
) {
    operator fun invoke() : Flow<Boolean>{
        return repository.isDarkModeEnabled
    }

}