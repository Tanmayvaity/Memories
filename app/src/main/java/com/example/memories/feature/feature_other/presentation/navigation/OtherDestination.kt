package com.example.memories.feature.feature_other.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_other.presentation.screens.CameraSettingsScreen
import com.example.memories.feature.feature_other.presentation.screens.OtherScreen
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createOtherGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<TopLevelScreen.Other>(
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        onBottomBarVisibilityChange(true)
        onFloatingActionBtnVisibilityChange(false)
        OtherScreen(
            onNavigateToTags = {route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.CameraSettings>{
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        CameraSettingsScreen(
            onBack = {
                navController.popBackStack()
            }
        )
    }



}