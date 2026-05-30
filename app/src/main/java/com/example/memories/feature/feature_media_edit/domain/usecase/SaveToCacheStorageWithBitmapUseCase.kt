package com.example.memories.feature.feature_media_edit.domain.usecase

import androidx.core.net.toUri
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

/**
 * Saves a list of media to cache storage and returns the resulting cache [UriType]s.
 *
 * - Images are rendered through their composed shader (when one is supplied) and rotation, then
 *   written to cache as a bitmap.
 * - Videos are copied to cache as-is (shaders/rotation don't apply).
 *
 * [shaderCodeList] and [degreesList] are positional, matching [mediaList] by index.
 */
class SaveToCacheStorageWithBitmapUseCase(
    val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(
        mediaList: List<UriType>,
        shaderCodeList: List<String?> = emptyList(),
        degreesList: List<Float> = emptyList()
    ): Result<List<UriType>> {
        val resultList = mediaList.mapIndexedNotNull { index, media ->
            val uri = media.uri?.toUri() ?: return@mapIndexedNotNull null

            if (media.type?.isVideoFile() == true) {
                val cachedUri = mediaRepository.saveToCacheStorageWithUri(uri).getOrNull()
                    ?: return Result.Error(Throwable("Returned Uri is null"))
                UriType(uri = cachedUri.toString(), type = media.type)
            } else {
                val degrees = degreesList.getOrElse(index) { 0f }
                val shaderCode = shaderCodeList.getOrNull(index)
                val bitmap = if (shaderCode == null) {
                    mediaRepository.uriToBitmap(uri, degrees).getOrNull()
                } else {
                    mediaRepository.applyFilterToUri(uri, shaderCode, degrees)
                } ?: return Result.Error(Throwable("Bitmap is null"))

                mediaRepository.saveToCacheStorage(uri, bitmap).getOrNull()
                    ?: return Result.Error(Throwable("Returned Uri is null"))
            }
        }
        return Result.Success(resultList)
    }
}
