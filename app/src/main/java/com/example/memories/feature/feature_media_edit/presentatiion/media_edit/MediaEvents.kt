package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.graphics.Bitmap
import android.net.Uri


sealed class MediaEvents {
    data class DownloadBitmap(val bitmap: Bitmap) : MediaEvents()
    data class UriToBitmap(
        val uri: Uri
    ) : MediaEvents()

    object BitmapToUri : MediaEvents()

//    data class Rotate(val bitmap : Bitmap) : MediaEvents()
//    data class ChangeBrightness(val bitmap : Bitmap) : MediaEvents()
//    data class Color( val bitmap : Bitmap) : MediaEvents()
}

