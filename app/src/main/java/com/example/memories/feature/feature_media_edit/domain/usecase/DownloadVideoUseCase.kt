package com.example.memories.feature.feature_media_edit.domain.usecase

import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DownloadVideoUseCase(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        uri : Uri
    ): Result<String> {
        return mediaRepository.downloadVideo(uri)
    }
}