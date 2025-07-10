package com.example.memories.feature.feature_camera.domain.repository

import android.content.Context
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import java.io.File

interface CameraRepository {
    fun setSurfaceCallback(callback: (SurfaceRequest) -> Unit)

    suspend fun bindToCamera(
        appContext : Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch : Boolean
    )

    fun torchToggle(torch : Boolean)

    fun zoom(scale : Float)

    suspend fun takePicture(file : File): CaptureResult

    fun setAspectRatio(aspect : AspectRatio)

    fun tapToFocus(offset: Offset)
}