package com.example.memories

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.view.navigation.Screen
import com.example.memories.view.navigation.TopLevelRoute
import com.example.memories.view.screens.CameraScreen
import com.example.memories.view.screens.FeedScreen
import com.example.memories.view.screens.NotificationScreen
import com.example.memories.view.screens.OtherScreen
import com.example.memories.view.screens.SearchScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoriesTheme {
                val navController = rememberNavController()
                AppNav(navController)
            }
        }
    }
}

@Composable
fun AppNav(navController: NavHostController) {


    var isBottomBarVisible by remember { mutableStateOf(true) }


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
                composable<Screen.Feed> {
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
                    isBottomBarVisible = false
                    CameraScreen()
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
    NavigationBar(containerColor = Color.White) {
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


@Preview(showBackground = true)
@Composable
fun AppNavPreview() {
    MemoriesTheme {
        AppNav(rememberNavController())
    }
}




