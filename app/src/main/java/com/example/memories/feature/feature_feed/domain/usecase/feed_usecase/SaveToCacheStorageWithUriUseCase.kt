package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import android.net.Uri
import com.example.memories.core.domain.repository.MediaRepository

class SaveToCacheStorageWithUriUseCase(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(uri : Uri) = mediaRepository.saveToCacheStorageWithUri(uri)

}