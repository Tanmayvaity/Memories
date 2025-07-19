package com.example.memories.feature.feature_feed.domain.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType.Companion.mapToType

@Immutable
data class MediaObject(
    val uri : Uri,
    val displayName : String,
    val bitmap : Bitmap,
    val type : Type = uri.mapToType()
)
