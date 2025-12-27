package com.example.memories.core.data.repository

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.domain.repository.ThemeRespository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val datastore : OtherSettingsDatastore
) : ThemeRespository {
    override val isDarkModeEnabled: Flow<Boolean> = datastore.isDarkModeEnabled
    override suspend fun setDarkMode(toDarkMode : Boolean ) {
        datastore.setDarkMode(toDarkMode)
    }

}