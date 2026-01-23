package com.example.memories.feature.feature_other.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_other.presentation.screens.AboutScreen
import com.example.memories.feature.feature_other.presentation.screens.CameraSettingsScreen
import com.example.memories.feature.feature_other.presentation.screens.DeleteAllDataRoot
import com.example.memories.feature.feature_other.presentation.screens.DeveloperInfoRoot
import com.example.memories.feature.feature_other.presentation.screens.OtherRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createOtherGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<TopLevelScreen.Other> {
        onBottomBarVisibilityChange(true)
        OtherRoot(
            onNavigateToTags = { route ->
                navController.navigate(route)
            },
            onNavigateToSettingsScreen = { route ->
                navController.navigate(route)
            },
            onNavigateToAboutScreen = {route ->
                navController.navigate(route)
            },
            onNavigateToDeveloperInfoScreen = {route ->
                navController.navigate(route)
            },
            onNavigateToDeleteAllDataScreen = {route ->
                navController.navigate(route)
            },
        )
    }

    composable<AppScreen.CameraSettings> {
        onBottomBarVisibilityChange(false)
        CameraSettingsScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }

    composable<AppScreen.About> {
        onBottomBarVisibilityChange(false)
        AboutScreen(
            onBack = {
                navController.popBackStack()
            },
        )
    }

    composable<AppScreen.DeveloperInfo> {
        onBottomBarVisibilityChange(false)
        DeveloperInfoRoot(
            onBack = {
                navController.popBackStack()
            }
        )
    }

    composable<AppScreen.DeleteAllData> {
        onBottomBarVisibilityChange(false)
        DeleteAllDataRoot(
            onBack = {
                navController.popBackStack()
            }
        )
    }


}