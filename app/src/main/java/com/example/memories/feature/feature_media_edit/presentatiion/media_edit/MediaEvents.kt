package com.example.memories.feature.feature_media_edit.presentatiion.media_edit

import android.net.Uri
import com.example.memories.core.domain.model.MediaActionType
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType


sealed class MediaEvents {
    data class DownloadMedia(val uri: Uri?, val page : Int, val degrees : Float = 0f) : MediaEvents()
//
    data class ShareImage(val uri : Uri?, val page : Int,val degrees: Float = 0f) : MediaEvents()
//

    data class ChangeRotation(val value : Float, val direction : RotationDirection,val page : Int) : MediaEvents()
//    data class DownloadVideo(val uri : Uri) : MediaEvents()
//    data class UriToBitmap(
//        val uri: Uri,
//        val page : Int
//    ) : MediaEvents()
//
//    data class OnRemoveBitmap(val page : Int) : MediaEvents()
//
    data class OnAdjustTypeValueClick(val page : Int) : MediaEvents()
//
//    object BitmapToUri : MediaEvents()
//
    data class EditToolStateChange(val tool : EditTool) : MediaEvents()
//
    data class AdjustTypeStateChange(val adjustType : AdjustType,val page : Int) : MediaEvents()
//
    data class AdjustTypeValueChange(val value : Float, val page : Int) : MediaEvents()
//
    data class FilterTypeStateChange(val filterType : FilterType,val page : Int) : MediaEvents()
//
    data class ApplyFilter(val page : Int,val filterType : FilterType) : MediaEvents()


    /**
     * Saves every media on screen into cache (images get their composed shader + rotation baked in,
     * videos are copied as-is) and then navigates to the Memory screen with the resulting cache
     * URIs. Operates on all pages of [EditorState.uriMap], so it takes no arguments.
     */
    object SaveMultipleImages : MediaEvents()

    data class AddMediaUri(val uriType: UriType, val position: Int) : MediaEvents()
    data class RemoveMediaUri(val position: Int) : MediaEvents()
    data class OpenDeviceCamera(val mediaType: MediaType) : MediaEvents()
    data class UpdateCurrentPosition(val position: Int) : MediaEvents()
    data class UpdateMediaActionType(val type: MediaActionType) : MediaEvents()
    data class UpdateMediaType(val mediaType: MediaType) : MediaEvents()
}

