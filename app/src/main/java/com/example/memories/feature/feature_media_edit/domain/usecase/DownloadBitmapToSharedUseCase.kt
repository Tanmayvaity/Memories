package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

/**
 * Saves an in-memory bitmap (e.g. a rendered "share memory" card) to shared storage / the device
 * gallery (MediaStore). Reuses the repository's bitmap-based download; no source URI needed.
 */
class DownloadBitmapToSharedUseCase(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<String> {
        return mediaRepository.downloadWithBitmap(bitmap)
    }
}
