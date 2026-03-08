package com.example.memories.feature.feature_memory.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class GenerateSharableUriUseCase (
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(isImage : Boolean) : Uri? {
        return mediaRepository.generateSharableUri(isImage)
    }
}