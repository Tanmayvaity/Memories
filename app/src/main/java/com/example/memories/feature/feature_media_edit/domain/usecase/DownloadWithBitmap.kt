package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DownloadWithBitmap(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        bitmap : Bitmap
    ): MediaResult {
        return mediaRepository.downloadWithBitmap(bitmap)
    }
}