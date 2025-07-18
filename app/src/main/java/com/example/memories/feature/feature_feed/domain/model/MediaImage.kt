package com.example.memories.feature.feature_feed.domain.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class MediaImage(
    val uri : Uri,
    val displayName : String,
    val bitmap : Bitmap
)