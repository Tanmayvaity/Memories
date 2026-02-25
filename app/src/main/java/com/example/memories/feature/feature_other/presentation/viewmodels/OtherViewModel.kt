package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.repository.AppSettingRepository
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.presentation.SettingClickEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherViewModel @Inject constructor(
    private val appSettingRepository: AppSettingRepository
) : ViewModel() {

    private val _settingEvents = MutableSharedFlow<SettingClickEvent>()
    val settingEvent = _settingEvents.asSharedFlow()

    private val _event = MutableSharedFlow<Boolean>()
    val event = _event.asSharedFlow()

    val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(false)


    val state = appSettingRepository.lockMethod
        .map { it ->
            OtherState(
                lockMethod = LockMethod.valueOf(it)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = OtherState()
        )


    fun settingClickEvent(event: SettingClickEvent) {
        viewModelScope.launch {
            _settingEvents.emit(event)
        }
    }

    fun onEvent(event: OtherEvents) {
        when (event) {
            is OtherEvents.CheckPinValidity -> {
                viewModelScope.launch {
                    isLoading.update { true }
                    val outcome = appSettingRepository.isCustomPinCorrect(event.pin)
                    _event.emit(outcome)
                    isLoading.update { false }
                }
            }
        }
    }

}


sealed interface OtherEvents {
    data class CheckPinValidity(val pin: String) : OtherEvents
}

data class OtherState(
    val lockMethod: LockMethod? = null,
)


