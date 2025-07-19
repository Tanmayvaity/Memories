package com.example.memories.feature.feature_camera.domain.usecase


import android.net.Uri
import coil3.util.CoilUtils.result
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository

class TakeMediaUseCase(
    val repository: CameraRepository
) {
    suspend operator fun invoke(
        cameraMode: CameraMode
    ): Result<Uri>{

        return if(cameraMode == CameraMode.PHOTO || cameraMode == CameraMode.PORTRAIT){
            repository.takePicture()
        }else{
            repository.takeVideo()
        }
    }
}



