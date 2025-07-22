package com.example.memories.feature.feature_other.presentation.viewmodels

import com.example.memories.feature.feature_camera.presentation.camera.CameraEvent

sealed class CameraSettingsEvents {
    data class ShutterSoundToggle(val value : Boolean) : CameraSettingsEvents()
    data class SaveLocationToggle(val value : Boolean) : CameraSettingsEvents()

    data class MirrorImageToggle(val value : Boolean) : CameraSettingsEvents()

    data class GridImageToggle(val value : Boolean): CameraSettingsEvents()

    data class FlipCameraToggle(val value : Boolean) : CameraSettingsEvents()

    data class WatermarkToggle(val value : Boolean): CameraSettingsEvents()

    data class HighEfficiencyPicturesToggle(val value : Boolean) : CameraSettingsEvents()

    data class HighEfficiencyVideosToggle(val value : Boolean) : CameraSettingsEvents()

    object Fetch : CameraSettingsEvents()



}