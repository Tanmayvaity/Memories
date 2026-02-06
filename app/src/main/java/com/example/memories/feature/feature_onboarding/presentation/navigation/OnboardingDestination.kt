package com.example.memories.feature.feature_onboarding.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.memories.feature.feature_onboarding.presentation.OnboardingRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.TopLevelScreen

fun NavGraphBuilder.createOnboardingGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
) {
    composable<AppScreen.Onboarding> {
        onBottomBarVisibilityChange(false)
        OnboardingRoot(
            onOnboardingComplete = {
                navController.navigate(TopLevelScreen.Feed) {
                    popUpTo<AppScreen.Onboarding> {
                        inclusive = true
                    }
                }
            }
        )
    }
}
