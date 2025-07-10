package com.example.memories.feature.feature_media_edit.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager
) : MediaRepository {
    override suspend fun uriToBitmap(
        uri: Uri
    ) : BitmapResult {
        return mediaManager.uriToBitmap(uri)
    }

    override suspend fun downloadWithBitmap(bitmap: Bitmap): MediaResult {
        return mediaManager.downloadImageWithBitmap(bitmap)
    }

    override suspend fun saveBitmapToInternalStorage(bitmap: Bitmap?): CaptureResult {
        return mediaManager.saveBitmapToInternalStorage(bitmap)
    }
}