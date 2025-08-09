package com.example.memories.feature.feature_feed.presentation

import android.net.Uri
import android.util.Size

sealed class MediaFeedEvent{
    object Feed : MediaFeedEvent()
    data class Delete(val uri : Uri): MediaFeedEvent()
    object DeleteMultiple : MediaFeedEvent()
    data class MediaSelect(val uri : Uri): MediaFeedEvent()
    data class MediaUnSelect(val uri : Uri): MediaFeedEvent()
    object MediaSelectedEmpty: MediaFeedEvent()

    object Share : MediaFeedEvent()
    object ShareMultiple : MediaFeedEvent()
    object ObserveMediaChanges:MediaFeedEvent()

    data class FetchThumbnail(val uri: Uri, val size : Size): MediaFeedEvent()
}
