package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType


sealed class MediaEvents {
    data class DownloadImage(val uri: Uri?, val page : Int,val degrees : Float = 0f) : MediaEvents()

    data class ShareImage(val uri : Uri?, val page : Int,val degrees: Float = 0f) : MediaEvents()

    data class SaveMultipleImages(val uriList : List<Uri?>,val page : Int) : MediaEvents()


    data class ChangeRotation(val value : Float, val direction : RotationDirection,val page : Int) : MediaEvents()
    data class DownloadVideo(val uri : Uri) : MediaEvents()
    data class UriToBitmap(
        val uri: Uri,
        val page : Int
    ) : MediaEvents()

    data class OnRemoveBitmap(val page : Int) : MediaEvents()

    data class OnAdjustTypeValueClick(val page : Int) : MediaEvents()

    object BitmapToUri : MediaEvents()

    data class EditToolStateChange(val tool : EditTool) : MediaEvents()

    data class AdjustTypeStateChange(val adjustType : AdjustType,val page : Int) : MediaEvents()

    data class AdjustTypeValueChange(val value : Float, val page : Int) : MediaEvents()

    data class FilterTypeStateChange(val filterType : FilterType,val page : Int) : MediaEvents()

    data class ApplyFilter(val page : Int) : MediaEvents()



}

