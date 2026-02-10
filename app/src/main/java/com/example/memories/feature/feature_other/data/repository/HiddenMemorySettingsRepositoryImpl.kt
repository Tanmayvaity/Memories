package com.example.memories.feature.feature_other.data.repository

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.domain.repository.HiddenMemorySettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HiddenMemorySettingsRepositoryImpl @Inject constructor(
    private val otherSettingsDatastore: OtherSettingsDatastore
) : HiddenMemorySettingsRepository {
    override val hiddenMemoryLockMethod: Flow<String>
        get() = otherSettingsDatastore.hiddenMemoriesLockMethod

    override val hiddenMemoryLockDuration: Flow<String>
        get() = otherSettingsDatastore.hiddenMemoriesLockDuration


    override suspend fun setHiddenMemoryLockMethod(method: LockMethod) {
        otherSettingsDatastore.setHiddenMemoriesLockMethod(method)
    }

    override suspend fun setHiddenMemoryLockDuration(duration: LockDuration) {
        otherSettingsDatastore.setHiddenMemoriesLockDuration(duration)
    }
}