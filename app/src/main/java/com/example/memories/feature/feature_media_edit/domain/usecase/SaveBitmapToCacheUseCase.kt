package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

/**
 * Saves an in-memory bitmap (e.g. a rendered "share memory" card) to cache and returns a
 * shareable FileProvider URI.
 */
class SaveBitmapToCacheUseCase(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): Result<Uri> {
        return mediaRepository.saveBitmapToCache(bitmap)
    }
}
