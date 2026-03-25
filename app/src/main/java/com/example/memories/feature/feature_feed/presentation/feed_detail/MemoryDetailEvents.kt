package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.net.Uri
import com.example.memories.core.domain.model.Type
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction

sealed class MemoryDetailEvents {
    data class Fetch(val id:String): MemoryDetailEvents()
    data class Action(val action: MemoryAction) : MemoryDetailEvents()

    data class DownloadMedia(val uri : Uri,val type : Type) : MemoryDetailEvents()

    data class ShareMedia(val uri : Uri) : MemoryDetailEvents()

    data class PlayVideo(val uri : Uri) : MemoryDetailEvents()

}

sealed class UiEvent{
    data class ShowToast(
        val message: String,
        val type : ToastType
    ) : UiEvent()

    data class ShowShareChooser(val value: Uri?) : UiEvent()
    data class ShowMediaChooser(val uri : Uri?) : UiEvent()
    data class Error(val message: String) : UiEvent()

    enum class ToastType{
        HIDDEN,
        DELETE,
        DOWNLOAD
    }

}
