package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.AppSettingRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    val repository: AppSettingRepository
) {
    suspend operator fun invoke( toDarkMode : Boolean){
         repository.setDarkMode(toDarkMode)
    }

}