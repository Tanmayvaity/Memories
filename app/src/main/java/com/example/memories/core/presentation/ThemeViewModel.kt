package com.example.memories.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.usecase.ThemeUseCase
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    val themeUseCase: ThemeUseCase
) : ViewModel(){
    private val _isDarkModeEnabled = MutableStateFlow<ThemeTypes>(ThemeTypes.LIGHT)
    val isDarkModeEnabled = _isDarkModeEnabled.asStateFlow()

    init {
        onEvent(ThemeEvents.GetTheme)
    }

    fun onEvent(event: ThemeEvents) {
        when(event){
            ThemeEvents.GetTheme ->{
                viewModelScope.launch {
                    val isDarkMode = themeUseCase.getThemeUseCase().collectLatest { value ->
                        _isDarkModeEnabled.update { if(value) ThemeTypes.DARK else ThemeTypes.LIGHT }

                    }

                }
            }
            is ThemeEvents.ChangeThemeType -> {
                _isDarkModeEnabled.update {
                    event.themeType
                }
            }
            is ThemeEvents.SetTheme -> {
                if(isDarkModeEnabled.value == ThemeTypes.SYSTEM)return
                viewModelScope.launch {
                    themeUseCase.setThemeUseCase(
                        isDarkModeEnabled.value == ThemeTypes.DARK
                    )
                }

            }
        }

    }


}