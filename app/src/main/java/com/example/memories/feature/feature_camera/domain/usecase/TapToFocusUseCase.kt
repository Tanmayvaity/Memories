package com.example.memories.feature.feature_camera.domain.usecase

import androidx.compose.ui.geometry.Offset
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class TapToFocusUseCase(
    val repository: CameraRepository
) {
    operator fun invoke(
        offset: Offset
    ) {
        repository.tapToFocus(offset)
    }
}