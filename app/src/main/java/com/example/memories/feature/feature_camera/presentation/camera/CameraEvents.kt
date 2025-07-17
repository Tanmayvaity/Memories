package com.example.memories.feature.feature_camera.presentation.camera

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import java.io.File

sealed class CameraEvent {
    object SurfaceCallback : CameraEvent()
    data class Preview(
        val app: Context,
        val lifecycleOwner: LifecycleOwner,
    ) : CameraEvent()
    object TorchToggle : CameraEvent()
    object ChangeLensFacing : CameraEvent()
    object PhotoMode : CameraEvent()
    object PortraitMode : CameraEvent()
    object VideoMode : CameraEvent()
    data class Zoom(val scale: Float) : CameraEvent()
    data class TakePicture(
        val file : File
    ): CameraEvent()

    data class Take(
        val context : Context,
        val file : File
    ): CameraEvent()
    object Reset : CameraEvent()
    object ToggleAspectRatio : CameraEvent()
    data class TapToFocus(val offset: Offset): CameraEvent()

    object Pause : CameraEvent()
    object Resume : CameraEvent()
    object Stop : CameraEvent()

    object Cancel : CameraEvent()
}