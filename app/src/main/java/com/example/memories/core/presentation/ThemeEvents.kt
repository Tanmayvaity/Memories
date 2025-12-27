package com.example.memories.core.presentation

import com.example.memories.feature.feature_other.presentation.ThemeTypes

sealed class ThemeEvents {
    object SetTheme : ThemeEvents()
    data class ChangeThemeType(val themeType: ThemeTypes) : ThemeEvents()
    object  GetTheme : ThemeEvents()
}