package com.example.memories.feature.feature_camera.presentation.camera.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_camera.presentation.camera.CameraRoot
import com.example.memories.navigation.AppScreen

@RequiresApi(Build.VERSION_CODES.S)
fun NavGraphBuilder.createCameraGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
){
    composable<AppScreen.Camera>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 3 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ){
        onBottomBarVisibilityChange(false)
        CameraRoot(
            onBack = { uri ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("media_uri",uri)
                navController.popBackStack()
            }
        )
    }

}