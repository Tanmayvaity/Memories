package com.example.memories.feature.feature_camera.domain.model

import android.net.Uri


sealed class VideoRecordingState {
    object Idle : VideoRecordingState()
    object Started : VideoRecordingState()
    object Paused : VideoRecordingState()
    object Resumed : VideoRecordingState()
    data class Finalized(val uri: Uri?) : VideoRecordingState()
    data class Error(val exception: Throwable) : VideoRecordingState()
}