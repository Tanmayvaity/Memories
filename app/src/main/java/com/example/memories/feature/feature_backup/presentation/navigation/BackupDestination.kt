package com.example.memories.feature.feature_backup.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_backup.presentation.BackupRoot
import com.example.memories.navigation.AppScreen

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