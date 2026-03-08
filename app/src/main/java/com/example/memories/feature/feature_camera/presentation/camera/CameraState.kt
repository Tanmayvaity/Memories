package com.example.memories.feature.feature_camera.presentation.camera

import androidx.camera.core.SurfaceRequest
import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_feed.presentation.feed_detail.UiEvent

data class CameraState(
    val surfaceRequest: SurfaceRequest? = null,
    val mode : CameraMode = CameraMode.PHOTO,
    val lensFacing : LensFacing = LensFacing.BACK,
    val torchState : Boolean = false,
    val zoomScale : Float = 0f,
//    val exposureValue : Int = 0,
    val aspectRatio : AspectRatio = AspectRatio.RATIO_4_3,
    val videoState : VideoState = VideoState.Idle,
    val cameraSettingsState : CameraSettingsState? = null,
    val timerMode : TimerMode = TimerMode.Idle,
    val timeElapsed : Long = 0L,
    val pictureTimerTimeElapsed : Int = 3,
    val cameraMediaSaving : Boolean = false
)

sealed class TimerMode{
    object Idle : TimerMode()
    object Started : TimerMode()
    object Running : TimerMode()
}


sealed class VideoState{
    object Idle : VideoState()
    object Started : VideoState()
    object Resume : VideoState()
    object Pause : VideoState()
    object Stop : VideoState()
}




