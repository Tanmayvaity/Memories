package com.example.memories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.memories.navigation.AppNavHost
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TOP_LEVEL_DESTINATIONS
import com.example.memories.navigation.TopLevelDestination
import com.example.memories.navigation.TopLevelNavigation
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState


@Preview
@Composable
fun AppNav(navController: NavHostController = rememberNavController()) {

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var isFloatingActionButtonVisible by remember { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelNavigation = remember(navController) { TopLevelNavigation(navController) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
//            contentWindowInsets = WindowInsets(top = 0.dp),
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                ) {
                    BottomNavBar(
                        navigateToTopLevelDestination = topLevelNavigation::navigateTo,
                        currentDestination = currentDestination,
                    )
                }
            },
            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//
//                    }
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_camera),
//                        contentDescription = "Camera"
//                    )
//                }
                AnimatedVisibility(
                    visible = isFloatingActionButtonVisible
                ) {
                    ExtendedFloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        text = {
                            Text(
                                text = "Create Memory"
                            )
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = "Camera"
                            )
                        },
                        onClick = {
                            navController.navigate(AppScreen.Camera)
                        },
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )

                }
//                if(isFloatingActionButtonVisible){
//                    ExtendedFloatingActionButton(
//                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
//                        text = {
//                            Text(
//                                text = "Create Memory"
//                            )
//                        },
//                        icon = {
//                            Icon(
//                                painter = painterResource(R.drawable.ic_camera),
//                                contentDescription = "Camera"
//                            )
//                        },
//                        onClick = {
//                            navController.navigate(AppScreen.Camera)
//                        }
//                    )
//                }


            }


        ) { innerPadding ->
            AppNavHost(
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
                navController = navController,
                onBottomBarVisibilityChange = { visibility ->
                    isBottomBarVisible = visibility
                },
                onFloatingActionBtnVisibilityChange = { visibility ->
                    isFloatingActionButtonVisible = visibility
                }

            )
        }
    }
}

@Preview
@Composable
fun BottomNavBar(
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit = {},
    currentDestination: NavDestination? = null,
) {


    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp
    ) {
        TOP_LEVEL_DESTINATIONS.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true,
                icon = {
                    Icon(
                        painter = painterResource(item.resource),
                        contentDescription = item.name
                    )
                },
                onClick = {
                    navigateToTopLevelDestination(item)
                },
                label = {
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
//                colors = NavigationBarItemDefaults.colors(
//                    indicatorColor = Color.Black,
//                    selectedIconColor = Color.White,
//                    selectedTextColor = Color.Black,
//                    unselectedIconColor = Color.Black,
//                    unselectedTextColor = Color.Black
//                )

                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

            )
        }
    }
}