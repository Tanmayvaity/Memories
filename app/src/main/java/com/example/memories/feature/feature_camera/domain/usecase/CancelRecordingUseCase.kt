package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class CancelRecordingUseCase(
    private val cameraRepository: CameraRepository

) {
    operator fun invoke(){
        cameraRepository.cancelRecording()
    }

}