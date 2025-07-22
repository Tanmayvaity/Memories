package com.example.memories.feature.feature_camera.presentation.camera

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import java.io.File

sealed class CameraEvent {
    object SurfaceCallback : CameraEvent()
    data class Preview(
        val lifecycleOwner: LifecycleOwner,
    ) : CameraEvent()
    object TorchToggle : CameraEvent()
    object ChangeLensFacing : CameraEvent()
    object PhotoMode : CameraEvent()
    object PortraitMode : CameraEvent()
    object VideoMode : CameraEvent()
    data class Zoom(val scale: Float) : CameraEvent()

    object  Take: CameraEvent()
    object Reset : CameraEvent()
    object ToggleAspectRatio : CameraEvent()
    data class TapToFocus(val offset: Offset): CameraEvent()

    object Pause : CameraEvent()
    object Resume : CameraEvent()
    object Stop : CameraEvent()

    object Cancel : CameraEvent()
    object Fetch : CameraEvent()
}