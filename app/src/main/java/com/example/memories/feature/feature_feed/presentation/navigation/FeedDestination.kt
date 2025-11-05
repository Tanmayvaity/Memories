package com.example.memories.feature.feature_feed.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_feed.presentation.feed.FeedRoot
import com.example.memories.feature.feature_feed.presentation.search.SearchScreen
import com.example.memories.feature.feature_feed.presentation.share.SharedRoute
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createFeedGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<TopLevelScreen.Feed> {
        onBottomBarVisibilityChange(true)
        onFloatingActionBtnVisibilityChange(true)
        FeedRoot(
            onCameraClick = {route ->
                navController.navigate(route)
            },
            onNavigateToImageEdit = {route ->
                navController.navigate(route)
            }
        )
    }
    composable<TopLevelScreen.Search> {
        onFloatingActionBtnVisibilityChange(false)
        onBottomBarVisibilityChange(true)
        SearchScreen(
            navigateToShared = { route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.Shared> {
        onBottomBarVisibilityChange(false)
        onFloatingActionBtnVisibilityChange(false)
        SharedRoute(
            onBack = {
                navController.popBackStack()
            }

        )
    }
}