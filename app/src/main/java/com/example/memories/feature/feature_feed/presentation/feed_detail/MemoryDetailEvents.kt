package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.net.Uri
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction
import com.example.memories.feature.feature_feed.presentation.feed.FeedEvents
import com.example.memories.feature.feature_media_edit.presentatiion.media_edit.MediaEditOneTimeEvents

sealed class MemoryDetailEvents {
    data class Fetch(val id:String): MemoryDetailEvents()
    data class Action(val action: MemoryAction) : MemoryDetailEvents()

    data class DownloadImage(val uri : Uri) : MemoryDetailEvents()

    data class ShareImage(val uri : Uri) : MemoryDetailEvents()
}

sealed class UiEvent{
    data class ShowToast(
        val message: String,
        val type : ToastType
    ) : UiEvent()

    data class ShowShareChooser(val value: Uri?) : UiEvent()
    data class Error(val message: String) : UiEvent()

    enum class ToastType{
        HIDDEN,
        DELETE,
        DOWNLOAD
    }

}
