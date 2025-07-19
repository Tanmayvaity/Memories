package com.example.memories.feature.feature_media_edit.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result

interface MediaRepository {
    suspend fun uriToBitmap(
        uri : Uri
    ): Result<Bitmap>

    suspend fun downloadWithBitmap(
        bitmap : Bitmap
    ): Result<String>

    suspend fun downloadVideo(
        uri : Uri
    ): Result<String>

    suspend fun saveBitmapToInternalStorage(
        bitmap: Bitmap?
    ): Result<Uri>
}