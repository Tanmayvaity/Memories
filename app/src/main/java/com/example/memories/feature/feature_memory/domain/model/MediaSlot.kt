package com.example.memories.feature.feature_memory.domain.model

import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.UriType

sealed class MediaSlot {
    data class New(val uriType: UriType) : MediaSlot()
    data class Existing(val mediaModel: MediaModel) : MediaSlot()
}
