package com.example.memories.feature.feature_media_edit.domain.model

import android.graphics.Bitmap

sealed class BitmapResult {
    data class Success(val bitmap : Bitmap) : BitmapResult()
    data class Error(val error:Throwable) : BitmapResult()
}

sealed class MediaResult {
    data class Success(val successMessage : String) : MediaResult()
    data class Error(val error:Throwable) : MediaResult()
}