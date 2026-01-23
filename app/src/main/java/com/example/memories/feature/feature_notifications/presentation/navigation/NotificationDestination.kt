package com.example.memories.feature.feature_notifications.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_memory.presentation.MemoryRoot
import com.example.memories.feature.feature_notifications.presentation.NotificationSettingsRoot
import com.example.memories.feature.feature_notifications.presentation.NotificationSettingsScreen
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.CustomNavType
import kotlin.reflect.typeOf

fun NavGraphBuilder.createNotificationGraph(
    navController : NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
){
    composable<AppScreen.Memory>(
        typeMap = mapOf(
            typeOf<UriType>() to CustomNavType.uriWrapperType,
            typeOf<Type>() to CustomNavType.mediaType,
            typeOf<List<UriType>>() to CustomNavType.uriWrapperListType
        ),
    ) {
        val args = it.toRoute<AppScreen.Memory>()
        onBottomBarVisibilityChange(false)
        MemoryRoot(
            onBackPress = {
                navController.popBackStack()
            },
            onGoToHomeScreen = {route ->
                navController.navigate(route){
                    popUpTo(navController.graph.startDestinationId){
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            onTagClick = {route ->
                navController.navigate(route)
            },
            uriList = args.uriTypeWrapperList
        )
    }

    composable<AppScreen.NotificationSettings> {
        onBottomBarVisibilityChange(false)
        NotificationSettingsRoot(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}