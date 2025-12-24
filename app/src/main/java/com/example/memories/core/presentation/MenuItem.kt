package com.example.memories.core.presentation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val title : String,
    @DrawableRes val icon : Int,
    val iconContentDescription : String,
    val onClick : () -> Unit = {}
)

data class ContextualMenuItem(
    val title : String,
    val selectedIcon : ImageVector,
    val unselectedIcon : ImageVector,
    val onClick: () -> Unit,
    val selectedIconContentDescription : String,
    val unselectedIconContentDescription : String
)