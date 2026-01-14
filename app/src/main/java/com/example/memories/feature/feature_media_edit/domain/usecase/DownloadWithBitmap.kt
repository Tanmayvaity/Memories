package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DownloadWithBitmap(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        uri : Uri?,
        shaderCode : String?,
        degrees : Float = 0f
    ): Result<String> {

        if(uri == null) return Result.Error(Throwable("Uri is null"))
        if(shaderCode == null) return mediaRepository.downloadWithBitmap(mediaRepository.uriToBitmap(uri, degrees).getOrNull() ?: return Result.Error(Throwable("Bitmap is null")))

        val bitmap = mediaRepository.applyFilterToUri(uri, shaderCode, degrees)
        if(bitmap == null) return Result.Error(Throwable("Bitmap is null"))
        return mediaRepository.downloadWithBitmap(bitmap)
    }
}