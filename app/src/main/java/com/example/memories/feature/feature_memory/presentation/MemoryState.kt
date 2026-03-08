package com.example.memories.feature.feature_memory.presentation

import android.net.Uri
import android.os.health.TimerStat
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType

data class MemoryState(
    val creationState: CreationState = CreationState.CREATE,
    val title: String = "",
    val content: String = "",
    val isTitleHintVisible: Boolean = true,
    val titleHintContent: String = "Write Title",
    val contentHintContent: String = "Write your story...",
    val isContentHintVisible: Boolean = true,
    val totalNumberOfTags: List<TagModel> = emptyList(),
    val tagsSelectedForThisMemory: List<TagModel> = emptyList(),
    val tagTextFieldValue: String = "",
    val uriMap: Map<Int, UriType> = emptyMap(),
    val memoryForTimeStamp: Long? = null,
    val timeStamp: Long? = null,
    val memory: MemoryWithMediaModel? = null,
    val originalMediaList: List<MediaModel> = emptyList(),
    val isLoading: Boolean = false,
    val tempMediaUri : String? = null,
    val currentPosition : Int? = null,
    val type : MediaActionType? = null
)


enum class CreationState {
    CREATE,
    UPDATE
}

enum class MediaActionType{
    DEVICE_CAMERA,
    CUSTOM_APP_CAMERA_FEATURE,
    PHOTO_PICKER,
    NONE
}