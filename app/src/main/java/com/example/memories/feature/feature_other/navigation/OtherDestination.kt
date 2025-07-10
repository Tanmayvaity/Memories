package com.example.memories.feature.feature_other.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_other.OtherScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createOtherGraph(
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<TopLevelScreen.Other> {
        onBottomBarVisibilityChange(true)
        onFloatingActionBtnVisibilityChange(false)
        OtherScreen()
    }
}