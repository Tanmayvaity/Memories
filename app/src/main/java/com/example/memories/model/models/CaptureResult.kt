package com.example.memories.model.models

import android.net.Uri

sealed class CaptureResult {
    data class Success(val uri : Uri?) : CaptureResult()
    data class Error(val error:Throwable) : CaptureResult()
}



