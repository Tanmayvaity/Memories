package com.example.memories.feature.feature_camera.presentation.camera.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_camera.presentation.camera.CameraRoute
import com.example.memories.navigation.AppScreen

@RequiresApi(Build.VERSION_CODES.S)
fun NavGraphBuilder.createCameraGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<AppScreen.Camera>(
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
    ){
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        CameraRoute(
            onNavigateToImageEdit = { route->
                navController.navigate(route)
            },
            onBack = {
                navController.popBackStack()
            }
        )
    }

}