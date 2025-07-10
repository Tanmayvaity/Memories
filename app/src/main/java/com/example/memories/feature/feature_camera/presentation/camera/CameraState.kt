package com.example.memories.feature.feature_camera.presentation.camera

import androidx.camera.core.SurfaceRequest
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.model.LensFacing

data class CameraState(
    val surfaceRequest: SurfaceRequest? = null,
    val mode : CameraMode = CameraMode.PHOTO,
    val lensFacing : LensFacing = LensFacing.BACK,
    val torchState : Boolean = false,
    val zoomScale : Float = 0f,
//    val exposureValue : Int = 0,
    val aspectRatio : AspectRatio = AspectRatio.RATIO_4_3
)

