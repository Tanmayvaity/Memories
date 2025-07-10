package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class TorchToggleUseCase(
    val repository: CameraRepository
) {
    operator fun invoke(
        torch: Boolean
    ) {
        repository.torchToggle(torch)
    }
}