package com.example.memories.navigation

import com.airbnb.lottie.L
import com.example.memories.core.domain.model.UriType
import kotlinx.serialization.Serializable


@Serializable
sealed class TopLevelScreen(val route: String) {

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

//    @Serializable
//    data class MediaEdit(
//        val uriTypeWrapperList : List<UriType>
//    ) : AppScreen("MediaEdit")

    @Serializable
    data object MediaEdit: AppScreen("MediaEdit")


    @Serializable
    data class Memory(
        val memoryId: String? = null,
//        val uriTypeWrapperList: List<UriType>
    ) : AppScreen("Memory")

    @Serializable
    object Shared : AppScreen("Shared")

    @Serializable
    object CameraSettings : AppScreen("CameraSettings")

    @Serializable
    data class MemoryDetail(
        val memoryId: String
    ) : AppScreen("MemoryDetail")

    @Serializable
    object Tags : AppScreen("Tags")

    @Serializable
    data class TagWithMemories(
        val id: String,
        val tagLabel: String
    ) : AppScreen("TagWithMemories")
}

