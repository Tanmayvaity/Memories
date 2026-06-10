package com.example.memories.feature.feature_media_edit.domain.usecase

import com.example.memories.feature.feature_media_edit.domain.model.ShaderStep
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.core.domain.repository.MediaRepository

/**
 * Produces a single [ShaderStep] that bakes the chosen [FilterType] together with all active
 * [AdjustType] values into one AGSL program, used for both the preview and download/share.
 */
class ComposeShaderUseCase(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(
        filterType: FilterType,
        adjustValues: Map<AdjustType, Float>
    ): ShaderStep {
        return mediaRepository.composeShader(filterType, adjustValues)
    }
}
