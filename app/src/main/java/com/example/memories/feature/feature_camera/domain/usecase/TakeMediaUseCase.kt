package com.example.memories.feature.feature_camera.domain.usecase


import android.net.Uri
import coil3.util.CoilUtils.result
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.mapToType
import com.example.memories.feature.feature_camera.domain.model.CameraMode
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import kotlin.map

class TakeMediaUseCase(
    val repository: CameraRepository
) {
    suspend operator fun invoke(
        cameraMode: CameraMode
    ): Result<UriType>{

        val resultUri = if(cameraMode == CameraMode.PHOTO || cameraMode == CameraMode.PORTRAIT){
            repository.takePicture()
        }else{
            repository.takeVideo()
        }

        return when(resultUri){
            is Result.Error -> {
                Result.Error(resultUri.error)
            }

            is Result.Success<Uri> -> {
                Result.Success(UriType(
                    uri = resultUri.data.toString(),
                    type = resultUri.data.mapToType()
                ))
            }
        }

    }
}



