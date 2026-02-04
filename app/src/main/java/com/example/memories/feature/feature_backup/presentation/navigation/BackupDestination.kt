package com.example.memories.feature.feature_backup.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_backup.presentation.BackupRoot
import com.example.memories.feature.feature_other.presentation.screens.AboutScreen
import com.example.memories.feature.feature_other.presentation.screens.CameraSettingsScreen
import com.example.memories.feature.feature_other.presentation.screens.DeleteAllDataRoot
import com.example.memories.feature.feature_other.presentation.screens.DeveloperInfoRoot
import com.example.memories.feature.feature_other.presentation.screens.OtherRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createBackupGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<AppScreen.Backup> {
        onBottomBarVisibilityChange(false)
        BackupRoot(
            onBack = {
                navController.popBackStack()
            }
        )
    }


}