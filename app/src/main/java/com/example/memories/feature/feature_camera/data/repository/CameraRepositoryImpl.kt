package com.example.memories.feature.feature_camera.data.repository

import android.content.Context
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.feature.feature_camera.data.data_source.CameraManager
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import java.io.File
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val cameraManager : CameraManager
) : CameraRepository {
    override fun setSurfaceCallback(callback: (SurfaceRequest) -> Unit) {
        cameraManager.setSurfaceRequestCallback(callback)
    }

    override suspend fun bindToCamera(
        appContext: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch : Boolean
    ) {
        cameraManager.bindToCamera(appContext,lifecycleOwner,lensFacing)
    }

    override fun torchToggle(torch: Boolean) {
        cameraManager.torchToggle(torch)
    }

    override fun zoom(scale : Float) {
        return cameraManager.zoom(scale)
    }

    override suspend  fun takePicture(file: File): CaptureResult {
        return cameraManager.takePicture(file)
    }

    override fun setAspectRatio(aspect: AspectRatio) {
        cameraManager.setAspectRatio(aspect)
    }

    override fun tapToFocus(offset: Offset) {
        cameraManager.tapToFocus(offset)
    }
}