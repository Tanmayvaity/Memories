package com.example.memories.navigation

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
        val uri: String
    ) : AppScreen("MediaEdit")

    @Serializable
    data class  Memory(
        val uri : String
    ) : AppScreen("Memory")

    @Serializable
    object Shared : AppScreen("Shared")

}

