package com.example.memories.feature.feature_feed.domain.usecaase

import android.net.Uri
import android.util.Size
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult

class GetMediaThumbnailUseCase(
    val repository: MediaFeedRepository
) {
    suspend operator fun invoke(
        uri: Uri,
        size: Size
    ): BitmapResult {
        return repository.getMediaThumbnail(uri,size)
    }

}