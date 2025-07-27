package com.example.memories.core.domain.usecase

import com.example.memories.core.data.repository.ThemeRepositoryImpl
import com.example.memories.core.domain.repository.ThemeRespository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    val repository: ThemeRespository
) {
    suspend operator fun invoke(){
         repository.setDarkMode()
    }

}