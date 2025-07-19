package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DownloadWithBitmap(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        bitmap : Bitmap
    ): Result<String> {
        return mediaRepository.downloadWithBitmap(bitmap)
    }
}