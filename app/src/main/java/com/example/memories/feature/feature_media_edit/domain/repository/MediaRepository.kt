package com.example.memories.feature.feature_media_edit.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.feature.feature_camera.domain.model.CaptureResult
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import com.example.memories.feature.feature_media_edit.domain.model.MediaResult

interface MediaRepository {
    suspend fun uriToBitmap(
        uri : Uri
    ): BitmapResult

    suspend fun downloadWithBitmap(
        bitmap : Bitmap
    ): MediaResult

    suspend fun downloadVideo(
        uri : Uri
    ): MediaResult

    suspend fun saveBitmapToInternalStorage(
        bitmap: Bitmap?
    ): CaptureResult
}