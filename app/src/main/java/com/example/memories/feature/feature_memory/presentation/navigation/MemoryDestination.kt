package com.example.memories.feature.feature_memory.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.feature.feature_memory.presentation.MemoryScreen
import com.example.memories.navigation.AppScreen

fun NavGraphBuilder.createMemoryGraph(
    navController : NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<AppScreen.Memory> {
        val it = it.toRoute<AppScreen.Memory>()
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
            uri = it.uri
        )
    }
}