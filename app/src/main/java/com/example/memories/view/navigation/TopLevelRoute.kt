package com.example.memories.view.navigation

import androidx.annotation.DrawableRes

data class TopLevelRoute<T : Any>(
    val name: String,
    val route: T,
    @DrawableRes
    val resource: Int
)