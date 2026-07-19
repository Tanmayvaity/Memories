package com.example.memories.feature.feature_firebase.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

const val IS_FIREBASE_ENABLED = false

fun NavGraphBuilder.createRemoteSyncGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {}
