package com.example.memories.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.memories.feature.feature_camera.presentation.camera.navigation.createCameraGraph
import com.example.memories.feature.feature_feed.presentation.navigation.createFeedGraph
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.navigation.createMediaEditGraph
import com.example.memories.feature.feature_memory.presentation.navigation.createMemoryGraph
import com.example.memories.feature.feature_other.presentation.navigation.createOtherGraph


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination : TopLevelScreen = TopLevelScreen.Feed,
    onBottomBarVisibilityChange : (Boolean)-> Unit,
    onFloatingActionBtnVisibilityChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ){
        createFeedGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onFloatingActionBtnVisibilityChange = onFloatingActionBtnVisibilityChange
        )

        createOtherGraph(
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onFloatingActionBtnVisibilityChange = onFloatingActionBtnVisibilityChange,
            navController = navController
        )

        createCameraGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onFloatingActionBtnVisibilityChange = onFloatingActionBtnVisibilityChange
        )

        createMediaEditGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onFloatingActionBtnVisibilityChange = onFloatingActionBtnVisibilityChange
        )

        createMemoryGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onFloatingActionBtnVisibilityChange = onFloatingActionBtnVisibilityChange
        )


    }
}