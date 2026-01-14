package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import javax.inject.Inject

class UriToBitmapUseCase @Inject constructor(
    val repository: MediaRepository
) {
    suspend operator fun invoke(
        uri : Uri,
        degrees : Float = 0f
    ): Result<Bitmap>{
        return repository.uriToBitmap(uri, degrees)
    }
}