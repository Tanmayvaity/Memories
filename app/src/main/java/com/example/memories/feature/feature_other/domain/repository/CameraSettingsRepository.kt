package com.example.memories.feature.feature_other.domain.repository

import com.example.memories.core.domain.model.CameraSettingsState
import kotlinx.coroutines.flow.Flow

interface CameraSettingsRepository {

    val enableShutterSound: Flow<Boolean>
    val enableSaveLocation: Flow<Boolean>
    val enableMirrorImage: Flow<Boolean>
    val enableGridLines: Flow<Boolean>
    val enableFlipCamera: Flow<Boolean>
    val enableWatermark: Flow<Boolean>
    val enableHeifPictures: Flow<Boolean>
    val enableHevcVideos: Flow<Boolean>

    suspend fun setShutterSoundSetting()
    suspend fun setSaveLocationSetting()
    suspend fun setMirrorImageSetting()
    suspend fun setGridLinesSeting()
    suspend fun setEnableFlipCameraSetting()
    suspend fun setWatermarkSetting()
    suspend fun setHeifPicturesSetting()
    suspend fun setHevcVideosSetting()

    suspend fun getState(): CameraSettingsState
}