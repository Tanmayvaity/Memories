package com.example.memories.feature.feature_camera.domain.usecase

import androidx.camera.core.SurfaceRequest
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class SetSurfaceCallbackUseCase(
    private val repository: CameraRepository
) {
    operator fun invoke(callback:(SurfaceRequest)-> Unit){
        repository.setSurfaceCallback(callback)
    }
}