package com.example.memories.feature.feature_feed.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.memories.core.util.isOnBackStack
import com.example.memories.feature.feature_feed.presentation.feed.FeedRoot
import com.example.memories.feature.feature_feed.presentation.feed_detail.MemoryDetailRoot
import com.example.memories.feature.feature_feed.presentation.history.HistoryRoot
import com.example.memories.feature.feature_feed.presentation.search.SearchRoot
import com.example.memories.feature.feature_feed.presentation.tags.TagsRoot
import com.example.memories.feature.feature_feed.presentation.tags_with_memory.TagWithMemoryRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.BASE_URL
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createFeedGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<TopLevelScreen.Feed> {
        onBottomBarVisibilityChange(true)
        FeedRoot(
            onCameraClick = { route ->
                navController.navigate(route)
            },
            onNavigateToImageEdit = { route ->
                navController.navigate(route)
            },
            onNavigateToMemoryDetail = { route ->
                navController.navigate(route)
            },
            onNavigateToMemoryCreate = { route ->
                navController.navigate(route)
            },
            onBottomBarVisibilityToggle = { it ->
                onBottomBarVisibilityChange(it)
            }
        )
    }
    composable<TopLevelScreen.Search> (
        deepLinks = listOf(
            navDeepLink<TopLevelScreen.Search>(basePath = "${BASE_URL}/search")
        )
    ){
        onBottomBarVisibilityChange(true)
        SearchRoot(
            onNavigateToMemoryDetail = { route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.MemoryDetail> {
        onBottomBarVisibilityChange(false)
        val args = it.toRoute<AppScreen.MemoryDetail>()
        MemoryDetailRoot(
            onBack = {
                navController.popBackStack()
            },
            onNavigateToMemory = { route ->
                navController.navigate(route)
            },
            onTagClick = {route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.Tags>{
        onBottomBarVisibilityChange(false)
        TagsRoot(
            onBack = {
                navController.popBackStack()
            },
            onNavigateToTagWithMemory = { route ->
                navController.navigate(route)
            }
        )
    }

    composable<AppScreen.TagWithMemories>{
        onBottomBarVisibilityChange(false)
        TagWithMemoryRoot(
            onBack = {
                navController.popBackStack()
            },
            onNavigateToMemory = {route ->
                navController.navigate(route)
            }

        )
    }

    composable<AppScreen.History> {
        onBottomBarVisibilityChange(false)
        HistoryRoot(
            onBack = {
                navController.popBackStack()
            },
            onNavigate = {route ->
                navController.navigate(route)
            }

        )
    }
}