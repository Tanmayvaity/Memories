package com.example.memories.feature.feature_camera.domain.usecase

import android.content.Context
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import java.io.File

class TakeVideoUseCase(
    val repository: CameraRepository
) {
    suspend operator fun invoke(
        context: Context,
        file: File
    ): CaptureResult {
        return repository.takeVideo(context,file)
    }
}

