package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class ZoomUseCase(
    val repository: CameraRepository
) {
    operator fun invoke(scale : Float){
        return repository.zoom(scale)
    }
}