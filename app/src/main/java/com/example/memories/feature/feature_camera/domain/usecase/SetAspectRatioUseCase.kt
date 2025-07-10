package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.feature.feature_camera.domain.model.AspectRatio
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class SetAspectRatioUseCase(
    val repository: CameraRepository
) {
    operator fun invoke(
        aspect : AspectRatio
    ) {
        repository.setAspectRatio(aspect)
    }
}