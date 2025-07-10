package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class SaveBitmapToInternalStorageUseCase(
    val repository: MediaRepository
) {
    suspend operator fun invoke(bitmap: Bitmap?): CaptureResult{
        return repository.saveBitmapToInternalStorage(bitmap)
    }
}