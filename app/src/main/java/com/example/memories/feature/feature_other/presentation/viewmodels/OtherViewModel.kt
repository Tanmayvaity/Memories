package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_other.presentation.SettingClickEvent
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class OtherViewModel : ViewModel() {

    private val _settingEvents = MutableSharedFlow<SettingClickEvent>()
    val settingEvent = _settingEvents.asSharedFlow()


    fun settingClickEvent(event: SettingClickEvent) {
        viewModelScope.launch {
            _settingEvents.emit(event)
        }
    }


}


