package com.example.memories.feature.feature_camera.data.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_camera.data.data_source.CameraManager
import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import javax.inject.Inject

class CameraRepositoryImpl @Inject constructor(
    private val cameraManager : CameraManager
) : CameraRepository {
    override fun setSurfaceCallback(callback: (SurfaceRequest) -> Unit) {
        cameraManager.setSurfaceRequestCallback(callback)
    }

    override suspend fun bindToCamera(
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch : Boolean
    ) {
        cameraManager.bindToCamera(lifecycleOwner,lensFacing)
    }

    override fun torchToggle(torch: Boolean) {
        cameraManager.torchToggle(torch)
    }

    override fun zoom(scale : Float) {
        return cameraManager.zoom(scale)
    }

    override suspend  fun takePicture(): Result<Uri> {
        return cameraManager.takePicture()
    }

    override fun setAspectRatio(aspect: AspectRatio) {
        cameraManager.setAspectRatio(aspect)
    }

    override fun tapToFocus(offset: Offset) {
        cameraManager.tapToFocus(offset)
    }

    @SuppressLint("MissingPermission")
    override suspend  fun takeVideo(): Result<Uri> {
        return cameraManager.takeVideo()
    }

    override fun pauseRecording() {
       cameraManager.pauseRecording()
    }

    override fun resumeRecording() {
        cameraManager.resumeRecording()
    }

    override fun stopRecording() {
        cameraManager.stopRecording()
    }

    override fun cancelRecording() {
        cameraManager.cancelRecording()
    }
}