package com.example.memories.feature.feature_feed.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.feature.feature_feed.presentation.feed.FeedRoot
import com.example.memories.feature.feature_feed.presentation.feed_detail.MemoryDetailRoot
import com.example.memories.feature.feature_feed.presentation.search.SearchRoot
import com.example.memories.feature.feature_feed.presentation.share.SharedRoute
import com.example.memories.feature.feature_feed.presentation.tags.TagsRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createFeedGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
    onFloatingActionBtnVisibilityChange : (Boolean) -> Unit
){
    composable<TopLevelScreen.Feed>(
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
        onFloatingActionBtnVisibilityChange(true)
        FeedRoot(
            onCameraClick = {route ->
                navController.navigate(route)
            },
            onNavigateToImageEdit = {route ->
                navController.navigate(route)
            },
            onNavigateToMemoryDetail = {route ->
                navController.navigate(route)
            },
            onNavigateToMemoryCreate = {route ->
                navController.navigate(route)
            },
            onBottomBarVisibilityToggle = onBottomBarVisibilityChange
        )
    }
    composable<TopLevelScreen.Search>(
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
        onFloatingActionBtnVisibilityChange(false)
        onBottomBarVisibilityChange(true)
        SearchRoot(
            onNavigateToMemoryDetail = {route ->
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

    composable<AppScreen.MemoryDetail>(
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
        val args = it.toRoute<AppScreen.MemoryDetail>()
        MemoryDetailRoot(
            onBack = {
                navController.popBackStack()
            },
            onNavigateToMemory = {route ->
                navController.navigate(route)
            }


        )
    }

    composable<AppScreen.Tags>(
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
        onBottomBarVisibilityChange(false)
        TagsRoot(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}