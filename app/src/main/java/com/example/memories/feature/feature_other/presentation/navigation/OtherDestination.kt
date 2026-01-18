package com.example.memories.feature.feature_other.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_other.presentation.screens.CameraSettingsScreen
import com.example.memories.feature.feature_other.presentation.screens.OtherScreen
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createOtherGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange: (Boolean) -> Unit
) {
    composable<TopLevelScreen.Other> {
        onBottomBarVisibilityChange(true)
        onFloatingActionBtnVisibilityChange(false)
        OtherScreen(
            onNavigateToTags = { route ->
                navController.navigate(route)
            },
            onNavigateToSettingsScreen = { route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.CameraSettings> {
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        CameraSettingsScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }


}