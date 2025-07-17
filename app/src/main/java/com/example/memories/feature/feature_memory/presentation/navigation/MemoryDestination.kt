package com.example.memories.feature.feature_memory.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.core.presentation.Type
import com.example.memories.core.presentation.UriType
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
            typeOf<Type>() to CustomNavType.mediaType
        )
    ) {
        val args = it.toRoute<AppScreen.Memory>()
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        MemoryScreen(
            onBackPress = {
                navController.popBackStack()
            },
            onCreateClick = { route ->
                navController.navigate(route){
                    popUpTo(navController.graph.startDestinationId){
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            uriType = args.uriTypeWrapper

        )
    }
}