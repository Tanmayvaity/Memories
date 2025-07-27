package com.example.memories.core.presentation

sealed class ThemeEvents {
    object SetTheme : ThemeEvents()
    object GetTheme : ThemeEvents()
}