package com.example.memories.feature.feature_other.presentation

enum class ThemeTypes{
    LIGHT,
    DARK,
    SYSTEM;

    fun toIndex() : Int {
        return when(this){
            ThemeTypes.LIGHT -> 0
            ThemeTypes.DARK -> 1
            ThemeTypes.SYSTEM -> 2
        }

    }
}