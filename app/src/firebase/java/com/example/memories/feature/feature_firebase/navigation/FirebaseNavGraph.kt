package com.example.memories.feature.feature_firebase.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_firebase.presentation.FirebaseRoot
import com.example.memories.navigation.AppScreen

const val IS_FIREBASE_ENABLED = true

fun NavGraphBuilder.createRemoteSyncGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<AppScreen.RemoteSync> {
        onBottomBarVisibilityChange(false)
        FirebaseRoot(
            onBack = { navController.popBackStack() }
        )
    }
}
