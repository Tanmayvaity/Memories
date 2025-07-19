package com.example.memories.feature.feature_camera.domain.repository

import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.LensFacing

interface CameraRepository {
    fun setSurfaceCallback(callback: (SurfaceRequest) -> Unit)

    suspend fun bindToCamera(
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch : Boolean
    )

    fun torchToggle(torch : Boolean)

    fun zoom(scale : Float)

    suspend fun takePicture(): Result<Uri>

    fun setAspectRatio(aspect : AspectRatio)

    fun tapToFocus(offset: Offset)

    suspend fun takeVideo(): Result<Uri>

    fun pauseRecording()
    fun resumeRecording()
    fun stopRecording()

    fun cancelRecording()
}