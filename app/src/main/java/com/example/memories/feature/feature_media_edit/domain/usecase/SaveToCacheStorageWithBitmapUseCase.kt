package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class SaveToCacheStorageWithBitmapUseCase(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        uriList: List<Uri?>,
        shaderCode: List<String?> = emptyList(),
        degreesList: List<Float> = emptyList()
    ): Result<List<Uri?>> {
        val resultList = uriList.mapIndexed { index, uri ->
            if (uri == null) return@mapIndexed uri
//        if(shaderCode == null) return mediaRepository.downloadWithBitmap(mediaRepository.uriToBitmap(uri).getOrNull() ?: return Result.Error(Throwable("Bitmap is null")))
            val degrees = if (degreesList.size > index) degreesList[index] else 0f

            val bitmap = if (shaderCode[index] == null) {

                mediaRepository.uriToBitmap(uri, degrees).getOrNull()
            } else {
                mediaRepository.applyFilterToUri(uri, shaderCode[index]!!, degrees)
            }
            if (bitmap == null) return Result.Error(Throwable("Bitmap is null"))
            mediaRepository.saveToCacheStorage(uri, bitmap).getOrNull() ?: return Result.Error(
                Throwable("Returned Uri is null")
            )
        }
        return Result.Success(resultList)
    }
}