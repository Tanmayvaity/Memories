package com.example.memories.feature.feature_other.domain.repository

import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.flow.Flow

interface HiddenMemorySettingsRepository {
    val hiddenMemoryLockMethod: Flow<String>
    val hiddenMemoryLockDuration: Flow<String>

    val isCustomPinSet : Flow<Boolean>

    suspend fun setHiddenMemoryLockMethod(method: LockMethod)
    suspend fun setHiddenMemoryLockDuration(duration: LockDuration)

    suspend fun setHiddenMemoryCustomPin(pin: String)

}
