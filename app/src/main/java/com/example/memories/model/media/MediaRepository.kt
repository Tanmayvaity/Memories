package com.example.memories.model.media


import android.content.Context
import android.graphics.Bitmap
import com.example.memories.model.models.MediaResult

class MediaRepository {

    val mediaManager by lazy { MediaManager() }

    suspend fun downloadImage(
        appContext: Context,
        uri: String
    ): MediaResult {

        return mediaManager.downloadImage(appContext,uri)

    }

    suspend fun downloadImageWithBitmap(
        appContext: Context,
        bitmap:Bitmap
    ): MediaResult {
        return mediaManager.downloadImageWithBitmap(appContext,bitmap)
    }







}