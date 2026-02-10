package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.domain.repository.HiddenMemorySettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HiddenMemorySettingViewModel @Inject constructor(
    private val hiddenMemorySettingsRepository: HiddenMemorySettingsRepository
) : ViewModel() {

    val state = combine(
        hiddenMemorySettingsRepository.hiddenMemoryLockMethod,
        hiddenMemorySettingsRepository.hiddenMemoryLockDuration
    ) { method, duration ->
        HiddenMemorySettingScreenState(
            currentLockMethod = LockMethod.valueOf(method),
            currentLockDuration = LockDuration.valueOf(duration)
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HiddenMemorySettingScreenState(
                currentLockMethod = LockMethod.NONE
            )
        )

    fun onEvent(event: HiddenMemorySettingEvents) {
        when (event) {
            is HiddenMemorySettingEvents.SetLockMethod -> {
                viewModelScope.launch {
                    hiddenMemorySettingsRepository.setHiddenMemoryLockMethod(event.method)
                }
            }

            is HiddenMemorySettingEvents.SetLockDuration -> {
                viewModelScope.launch {
                    hiddenMemorySettingsRepository.setHiddenMemoryLockDuration(event.duration)
                }
            }
        }
    }


}


sealed class HiddenMemorySettingEvents {
    data class SetLockMethod(val method: LockMethod) : HiddenMemorySettingEvents()
    data class SetLockDuration(val duration : LockDuration) : HiddenMemorySettingEvents()
}


data class HiddenMemorySettingScreenState(
    val currentLockMethod: LockMethod = LockMethod.NONE,
    val currentLockDuration: LockDuration = LockDuration.ONE_MINUTE
)