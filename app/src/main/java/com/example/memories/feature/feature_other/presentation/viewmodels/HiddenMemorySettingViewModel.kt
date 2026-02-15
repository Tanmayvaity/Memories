package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.domain.repository.HiddenMemorySettingsRepository
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingEvents.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HiddenMemorySettingViewModel @Inject constructor(
    private val hiddenMemorySettingsRepository: HiddenMemorySettingsRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {


    private val _events = Channel<HiddenScreenOneTimeEvent>()
    val events = _events.receiveAsFlow()


    val state = combine(
        hiddenMemorySettingsRepository.hiddenMemoryLockMethod,
        hiddenMemorySettingsRepository.hiddenMemoryLockDuration,
        hiddenMemorySettingsRepository.isCustomPinSet
    ) { method, duration , isCustomPinSet ->
        HiddenMemorySettingScreenState(
            currentLockMethod = LockMethod.valueOf(method),
            currentLockDuration = LockDuration.valueOf(duration),
            isCustomPinSet = isCustomPinSet
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
                    if(event.method != LockMethod.CUSTOM_PIN && state.value.isCustomPinSet){
                        onEvent(SetCustomPin(""))
                    }
                }
            }

            is HiddenMemorySettingEvents.SetLockDuration -> {
                viewModelScope.launch {
                    hiddenMemorySettingsRepository.setHiddenMemoryLockDuration(event.duration)
                }
            }
            is HiddenMemorySettingEvents.SetCustomPin -> {
                viewModelScope.launch {
                    hiddenMemorySettingsRepository.setHiddenMemoryCustomPin(event.pin)
                }
            }

            HiddenMemorySettingEvents.DeleteAllHiddenMemories -> {
                viewModelScope.launch {
                    memoryRepository.deleteAllHiddenMemories()
                    _events.send(HiddenScreenOneTimeEvent.ShowToast("All hidden memories deleted"))
                }
            }
            HiddenMemorySettingEvents.UnHideAllHiddenMemories -> {
                viewModelScope.launch {
                    memoryRepository.unHideAllHiddenMemories()
                    _events.send(HiddenScreenOneTimeEvent.ShowToast("All hidden memories unhidden"))
                }

            }
        }
    }


}


sealed class HiddenMemorySettingEvents {
    data class SetLockMethod(val method: LockMethod) : HiddenMemorySettingEvents()
    data class SetLockDuration(val duration : LockDuration) : HiddenMemorySettingEvents()

    data class SetCustomPin(val pin : String) : HiddenMemorySettingEvents()
    
    object DeleteAllHiddenMemories : HiddenMemorySettingEvents()
    
    object UnHideAllHiddenMemories : HiddenMemorySettingEvents()

}


sealed interface HiddenScreenOneTimeEvent {
    data class ShowToast(val message: String) : HiddenScreenOneTimeEvent
}


data class HiddenMemorySettingScreenState(
    val currentLockMethod: LockMethod = LockMethod.NONE,
    val currentLockDuration: LockDuration = LockDuration.ONE_MINUTE,
    val isCustomPinSet : Boolean = false
)