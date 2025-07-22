package com.example.memories.feature.feature_camera.domain.usecase

import com.example.memories.feature.feature_other.domain.usecase.CameraSettingsUseCase
import javax.inject.Inject

data class CameraUseCases @Inject constructor(
    val setSurfaceCallbackUseCase: SetSurfaceCallbackUseCase,
    val bindToCameraUseCase: BindToCameraUseCase,
    val torchToggleUseCase: TorchToggleUseCase,
    val zoomRangeUseCase: ZoomUseCase,
    val takeMediaUseCase: TakeMediaUseCase,
    val setAspectRatioUseCase: SetAspectRatioUseCase,
    val tapToFocusUseCase: TapToFocusUseCase,
    val resumeRecordingUseCase: ResumeRecordingUseCase,
    val pauseRecordingUseCase: PauseRecordingUseCase,
    val stopRecordingUseCase: StopRecordingUseCase,
    val cancelRecordingUseCase: CancelRecordingUseCase,
    val cameraSettingsUseCase: GetCameraSettingsUseCase
)