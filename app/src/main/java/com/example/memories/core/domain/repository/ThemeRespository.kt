package com.example.memories.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRespository  {
    val isDarkModeEnabled : Flow<Boolean>
    suspend fun setDarkMode()

}