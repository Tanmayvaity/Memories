package com.example.memories.feature.feature_camera.domain.usecase


import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import java.io.File

class TakePictureUseCase(
    val repository: CameraRepository
) {
    suspend operator fun invoke(
        file : File
    ): CaptureResult{
        return repository.takePicture(file)
    }
}