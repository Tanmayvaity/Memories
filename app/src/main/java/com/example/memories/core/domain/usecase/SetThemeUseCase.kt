package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.ThemeRespository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    val repository: ThemeRespository
) {
    suspend operator fun invoke( toDarkMode : Boolean){
         repository.setDarkMode(toDarkMode)
    }

}