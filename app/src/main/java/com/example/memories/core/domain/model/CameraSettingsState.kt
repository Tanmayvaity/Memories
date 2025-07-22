package com.example.memories.core.domain.model

data class CameraSettingsState(
    val shutterSound: Boolean = true,
    val saveLocation : Boolean = false,
    val mirrorImage : Boolean = true,
    val gridLines : Boolean = false,
    val flipCameraUsingSwipe : Boolean = false,
    val watermark : Boolean = false,
    val heifPictures : Boolean = false,
    val hevcVideos : Boolean = false
)