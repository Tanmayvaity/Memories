package com.example.memories.feature.feature_media_edit.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.feature.feature_media_edit.domain.model.ShaderStep
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.feature.feature_media_edit.data.ShaderComposer
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.data.ComposedStep
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
) : MediaRepository {
    override suspend fun uriToBitmap(
        uri: Uri,
        degrees: Float
    ) : Result<Bitmap> {
        return mediaManager.uriToBitmap(uri, degrees)
    }

    override suspend fun downloadWithBitmap(bitmap: Bitmap): Result<String> {
        return mediaManager.downloadImageWithBitmap(bitmap)
    }

    override suspend fun downloadVideo(uri: Uri): Result<String> {
        return mediaManager.downloadVideo(uri)
    }

    override suspend fun saveBitmapToInternalStorage(bitmap: Bitmap?): Result<Uri> {
        return mediaManager.saveBitmapToInternalStorage(bitmap)
    }

    override suspend fun saveBitmapToCache(bitmap: Bitmap): Result<Uri> {
        return mediaManager.saveBitmapToCacheStorage(bitmap)
    }

    override suspend fun deleteMedia(uriList: List<Uri>) : Result<String> {
        return mediaManager.deleteInternalMedia(uriList)
    }

    override fun composeShader(
        filterType: FilterType,
        adjustValues: Map<AdjustType, Float>
    ): ShaderStep {
        return ComposedStep(ShaderComposer.compose(filterType, adjustValues))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun applyFilterToUri(uri: Uri, shaderCode: String, degrees : Float): Bitmap? {
        return mediaManager.applyFilter(uri,shaderCode,degrees)
    }

    override suspend fun saveToCacheStorage(uri: Uri, bitmap: Bitmap): Result<UriType> {
        return mediaManager.saveToCacheStorage(uri,bitmap)
    }

    override suspend fun saveToCacheStorageWithUri(uri: Uri): Result<Uri> {
        return mediaManager.saveToCacheStorageWithUri(uri)
    }

    override fun generateShareableUri(isImage: Boolean?,uri: Uri?): Uri? {
        return mediaManager.generateShareableUri(isImage,uri)
    }


}