package com.example.memories.feature.feature_camera.domain.model

import android.net.Uri

sealed class CaptureResult {
    data class Success(val uri : Uri?) : CaptureResult()
    data class Error(val error:Throwable) : CaptureResult()
}



