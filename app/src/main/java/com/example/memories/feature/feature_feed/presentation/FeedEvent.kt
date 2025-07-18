package com.example.memories.feature.feature_feed.presentation

import android.net.Uri
import android.util.Size

sealed class FeedEvent{
    object Feed : FeedEvent()
    data class Delete(val uri : Uri): FeedEvent()
    object DeleteMultiple : FeedEvent()
    data class MediaSelect(val uri : Uri): FeedEvent()
    data class MediaUnSelect(val uri : Uri): FeedEvent()
    object MediaSelectedEmpty: FeedEvent()

    object Share : FeedEvent()
    object ShareMultiple : FeedEvent()
    object ObserveMediaChanges:FeedEvent()

    data class FetchThumbnail(val uri: Uri,val size : Size): FeedEvent()
}
