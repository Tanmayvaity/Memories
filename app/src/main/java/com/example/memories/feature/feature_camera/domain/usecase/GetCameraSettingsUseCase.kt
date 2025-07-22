package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.core.domain.model.CameraSettingsState
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class GetCameraSettingsUseCase(
    val cameraRepository: CameraRepository
) {
    suspend operator fun invoke(): CameraSettingsState{
        return cameraRepository.getCameraSettingsState()
    }
}