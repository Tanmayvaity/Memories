package com.example.memories.feature.feature_media_edit.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import javax.inject.Inject

class UriToBitmapUseCase @Inject constructor(
    val repository: MediaRepository
) {
    suspend operator fun invoke(
        uri : Uri
    ): BitmapResult{
        return repository.uriToBitmap(uri)
    }
}