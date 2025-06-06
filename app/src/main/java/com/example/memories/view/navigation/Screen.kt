package com.example.memories.view.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Feed : Screen("Feed")

    @Serializable
    object Search : Screen("Search")

    @Serializable
    object Notification : Screen("Notification")

    @Serializable
    object Other : Screen("Other")

    @Serializable
    object Camera : Screen("Camera")

    @Serializable
    data class ImageEdit(
        val uri: String
    ) : Screen("ImageEdit")

    companion object {
        fun fromRoute(route: String): Screen? = when (route) {
            Feed.route -> Feed
            Search.route -> Search
            Notification.route -> Notification
            Other.route -> Other
            Camera.route -> Camera
            else -> null
        }
    }
}