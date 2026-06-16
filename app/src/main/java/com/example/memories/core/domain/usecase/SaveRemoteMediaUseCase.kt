package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.repository.MediaRepository

class SaveRemoteMediaUseCase(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(url: String, isImage: Boolean): Result<UriType> {
        return mediaRepository.saveRemoteMediaToCache(url, isImage)
    }
}
