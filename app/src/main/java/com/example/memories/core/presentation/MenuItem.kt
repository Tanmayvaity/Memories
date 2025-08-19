package com.example.memories.core.presentation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val title : String,
    @DrawableRes val icon : Int,
    val iconContentDescription : String,
    val onClick : () -> Unit = {}
)