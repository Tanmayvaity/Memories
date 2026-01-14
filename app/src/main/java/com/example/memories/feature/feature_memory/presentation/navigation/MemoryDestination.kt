package com.example.memories.feature.feature_memory.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.isOnBackStack
import com.example.memories.feature.feature_memory.presentation.MemoryRoot
import com.example.memories.feature.feature_memory.presentation.MemoryScreen
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.CustomNavType
import kotlin.reflect.typeOf

fun NavGraphBuilder.createMemoryGraph(
    navController : NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<AppScreen.Memory>(
        typeMap = mapOf(
            typeOf<UriType>() to CustomNavType.uriWrapperType,
            typeOf<Type>() to CustomNavType.mediaType,
            typeOf<List<UriType>>() to CustomNavType.uriWrapperListType
        ),
    ) {
        val args = it.toRoute<AppScreen.Memory>()
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        MemoryRoot(
            onBackPress = {
                navController.popBackStack()
            },
            onGoToHomeScreen = {route ->
                navController.navigate(route){
                    popUpTo(navController.graph.startDestinationId){
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            onTagClick = {route ->
                navController.navigate(route)
            },
            uriList = args.uriTypeWrapperList
        )
    }
}