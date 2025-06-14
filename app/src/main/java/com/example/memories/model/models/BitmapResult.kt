package com.example.memories.model.models

import android.graphics.Bitmap

sealed class BitmapResult {
    data class Success(val bitmap : Bitmap) : BitmapResult()
    data class Error(val error:Throwable) : BitmapResult()
}