package com.example.memories.feature.feature_camera.domain.usecase

import android.content.Context
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import java.io.File

class PauseRecordingUseCase(
    val repository: CameraRepository
) {
    operator fun invoke() {
        return repository.pauseRecording()
    }
}

