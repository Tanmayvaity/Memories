package com.example.memories.model.media


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.model.models.BitmapResult
import com.example.memories.model.models.CaptureResult
import com.example.memories.model.models.MediaImage
import com.example.memories.model.models.MediaResult
import kotlinx.coroutines.flow.Flow
import java.io.File

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



    suspend fun copyFromSharedStorage(
        context : Context,
        sharedUri : Uri,
        file : File
    ):CaptureResult{
        return mediaManager.copyFromSharedStorage(context, sharedUri,file)
    }

    suspend fun uriToBitmap(uri:Uri,context:Context): BitmapResult {
        return mediaManager.uriToBitmap(uri,context)
    }

    suspend fun saveToInternalStorage(
        file:File,
        bitmap : Bitmap
    ):CaptureResult{
        return mediaManager.saveBitmapToInternalStorage(bitmap,file)
    }



    suspend fun fetchMediaFromShared(context : Context,fromApp:Boolean): Flow<MediaImage> {
        return mediaManager.fetchMediaFromShared(context,fromApp)
    }









}