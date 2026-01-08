package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.graphics.Bitmap
import android.net.Uri


sealed class MediaEvents {
    data class DownloadBitmap(val bitmap: Bitmap) : MediaEvents()
    data class DownloadVideo(val uri : Uri) : MediaEvents()
    data class UriToBitmap(
        val uri: Uri
    ) : MediaEvents()

    object BitmapToUri : MediaEvents()

    data class EditToolStateChange(val tool : EditTool) : MediaEvents()
}

