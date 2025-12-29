package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaEditScreen
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.CustomNavType
import kotlin.reflect.typeOf

fun NavGraphBuilder.createMediaEditGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<AppScreen.MediaEdit>(
        typeMap = mapOf(
            typeOf<UriType>() to CustomNavType.uriWrapperType,
            typeOf<Type>() to CustomNavType.mediaType
        )
    ) {
        val args = it.toRoute<AppScreen.MediaEdit>()
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        MediaEditScreen(
            uriType = args.uriTypeWrapper,
            onBackPress = {
                navController.popBackStack()
            },
            onNextClick = { route ->
                navController.navigate(route)
            }
        )
    }
}