package com.example.memories.feature.feature_media_edit.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager
) : MediaRepository {
    override suspend fun uriToBitmap(
        uri: Uri
    ) : Result<Bitmap> {
        return mediaManager.uriToBitmap(uri)
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
}