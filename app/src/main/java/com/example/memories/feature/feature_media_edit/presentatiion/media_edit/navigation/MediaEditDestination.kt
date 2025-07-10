package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaEditScreen
import com.example.memories.navigation.AppScreen

fun NavGraphBuilder.createMediaEditGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<AppScreen.MediaEdit>(
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        val args = it.toRoute<AppScreen.MediaEdit>()
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        MediaEditScreen(
            uri = args.uri,
            onBackPress = {
                navController.popBackStack()
            },
            onNextClick = { route ->
                navController.navigate(route)
            }
        )
    }
}