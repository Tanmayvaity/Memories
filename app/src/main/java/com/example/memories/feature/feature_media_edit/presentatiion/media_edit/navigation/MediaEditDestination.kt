package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.presentation.media_edit.MediaEditRoot
import com.example.memories.navigation.AppScreen
import com.example.memories.navigation.BASE_URL
import com.example.memories.navigation.CustomNavType
import kotlin.reflect.typeOf

fun NavGraphBuilder.createMediaEditGraph(
    navController: NavHostController,
    onBottomBarVisibilityChange : (Boolean) -> Unit,
){
    composable<AppScreen.MediaEdit>(
        typeMap = mapOf(
            typeOf<UriType>() to CustomNavType.uriWrapperType,
            typeOf<Type>() to CustomNavType.mediaType,
            typeOf<List<UriType>>() to CustomNavType.uriWrapperListType
        ),
        deepLinks = listOf(
            navDeepLink<AppScreen.MediaEdit>(basePath = "${BASE_URL}/edit")
        )
    ) {
        val args = it.toRoute<AppScreen.MediaEdit>()
        val mediaUri by it.savedStateHandle
            .getStateFlow<String?>("media_uri", null)
            .collectAsStateWithLifecycle()
        onBottomBarVisibilityChange(false)
        MediaEditRoot(
            onBackPress = {
                navController.popBackStack()
            },
            onNextClick = { route ->
                navController.navigate(route)
            },
            onNavigateToCamera = { route ->
                navController.navigate(route)
            },
            takenUri = mediaUri,
        )
    }
}