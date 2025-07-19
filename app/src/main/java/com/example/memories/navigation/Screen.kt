package com.example.memories.navigation

import com.example.memories.core.domain.model.UriType
import kotlinx.serialization.Serializable



@Serializable
sealed class TopLevelScreen(val route : String){

    @Serializable
    object Feed : TopLevelScreen("Feed")

    @Serializable
    object Search : TopLevelScreen("Search")

    @Serializable
    object Other : TopLevelScreen("Other")
}

@Serializable
sealed class AppScreen(val route: String) {
    object Notification : AppScreen("Notification")

    @Serializable
    object Camera : AppScreen("Camera")

    @Serializable
    data class MediaEdit(
        val uriTypeWrapper : UriType
    ) : AppScreen("MediaEdit")

    @Serializable
    data class  Memory(
        val uriTypeWrapper: UriType
    ) : AppScreen("Memory")

    @Serializable
    object Shared : AppScreen("Shared")

}

