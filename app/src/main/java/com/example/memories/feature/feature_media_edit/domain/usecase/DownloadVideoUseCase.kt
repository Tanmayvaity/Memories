package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DownloadVideoUseCase(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        uri : Uri
    ): MediaResult {
        return mediaRepository.downloadVideo(uri)
    }
}