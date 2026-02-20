package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingEvents.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HiddenMemorySettingViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository,
    private val memoryRepository: MemoryRepository
) : ViewModel() {


    private val _events = Channel<HiddenScreenOneTimeEvent>()
    val events = _events.receiveAsFlow()


    val state = combine(
        appSettingRepository.hiddenMemoryLockMethod,
        appSettingRepository.hiddenMemoryLockDuration,
        appSettingRepository.isCustomPinSet
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
                    appSettingRepository.setHiddenMemoryLockMethod(event.method)
                    if(event.method != LockMethod.CUSTOM_PIN && state.value.isCustomPinSet){
                        onEvent(SetCustomPin(""))
                    }
                }
            }

            is HiddenMemorySettingEvents.SetLockDuration -> {
                viewModelScope.launch {
                    appSettingRepository.setHiddenMemoryLockDuration(event.duration)
                }
            }
            is HiddenMemorySettingEvents.SetCustomPin -> {
                viewModelScope.launch {
                    appSettingRepository.setHiddenMemoryCustomPin(event.pin)
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