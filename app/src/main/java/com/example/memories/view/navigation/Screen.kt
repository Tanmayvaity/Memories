package com.example.memories.view.navigation

import kotlinx.serialization.Serializable

sealed class Screen{
    @Serializable
    object Feed : Screen()

    @Serializable
    object Search : Screen()

    @Serializable
    object Notification : Screen()

    @Serializable
    object Other : Screen()

    @Serializable
    object Camera : Screen()
}