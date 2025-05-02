package com.example.memories.view.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.memories.R
import com.example.memories.view.screens.CameraScreen
import com.example.memories.view.screens.FeedScreen
import com.example.memories.view.screens.NotificationScreen
import com.example.memories.view.screens.OtherScreen
import com.example.memories.view.screens.SearchScreen

@Composable
fun AppNav(navController: NavHostController) {


    var isBottomBarVisible by remember { mutableStateOf(true) }

    val density = LocalDensity.current


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    BottomNavBar(navController = navController)
                }

            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Feed,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Screen.Feed>{
                    isBottomBarVisible = true
                    FeedScreen()
                }
                composable<Screen.Search> {
                    isBottomBarVisible = true
                    SearchScreen()
                }
                composable<Screen.Notification> {
                    isBottomBarVisible = true
                    NotificationScreen()
                }
                composable<Screen.Other> {
                    isBottomBarVisible = true
                    OtherScreen()
                }
                composable<Screen.Camera> {
                    isBottomBarVisible = true
                    CameraScreen(
                        popBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }


        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val topLevelRoutes = listOf(
        TopLevelRoute<Screen.Feed>(Screen.Feed.route, Screen.Feed, R.drawable.ic_feed),
        TopLevelRoute<Screen.Search>(Screen.Search.route, Screen.Search, R.drawable.ic_search),
        TopLevelRoute<Screen.Camera>(Screen.Camera.route, Screen.Camera, R.drawable.ic_camera),
        TopLevelRoute<Screen.Notification>(
            Screen.Notification.route,
            Screen.Notification,
            R.drawable.ic_notification
        ),
        TopLevelRoute<Screen.Other>(Screen.Other.route, Screen.Other, R.drawable.ic_other),

        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar(contentColor = Color.White) {
        topLevelRoutes.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                icon = {
                    Icon(
                        painter = painterResource(item.resource),
                        contentDescription = item.name
                    )
                },
//                label = {
//                    Text(text = item.name)
//                },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true

                    }

                },
            )
        }
    }
}