package com.example.memories.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.memories.feature.feature_backup.presentation.navigation.createBackupGraph
import com.example.memories.feature.feature_notifications.presentation.navigation.createNotificationGraph
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
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ){
        createFeedGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        )

        createOtherGraph(
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            navController = navController
        )

        createCameraGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        )

        createMediaEditGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        )

        createMemoryGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        )

        createNotificationGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
        )

        createBackupGraph(
            navController = navController,
            onBottomBarVisibilityChange = onBottomBarVisibilityChange
        )


    }
}