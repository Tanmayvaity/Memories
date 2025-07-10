package com.example.memories.feature.feature_camera.domain.usecase

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.memories.feature.feature_camera.domain.model.LensFacing
import com.example.memories.feature.feature_camera.domain.repository.CameraRepository
import javax.inject.Inject

class BindToCameraUseCase @Inject constructor(
    private val repository: CameraRepository
) {
    suspend operator fun invoke(
        appContext: Context,
        lifecycleOwner: LifecycleOwner,
        lensFacing: LensFacing,
        torch: Boolean
    ){
        repository.bindToCamera(appContext,lifecycleOwner,lensFacing,torch)
    }
}