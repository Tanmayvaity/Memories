package com.example.memories.feature.feature_feed.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository

class GetMediaThumbnailUseCase(
    val repository: MediaFeedRepository
) {
    suspend operator fun invoke(
        uri: Uri,
        size: Size
    ): Result<Bitmap> {
        return repository.getMediaThumbnail(uri,size)
    }

}