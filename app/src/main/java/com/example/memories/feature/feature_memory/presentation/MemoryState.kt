package com.example.memories.feature.feature_memory.presentation

import android.os.health.TimerStat
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType

data class MemoryState(
    val creationState : CreationState = CreationState.CREATE,
    val title : String = "",
    val content : String = "",
    val isTitleHintVisible : Boolean = true,
    val titleHintContent : String = "Write Title",
    val contentHintContent : String = "Write your story...",
    val isContentHintVisible : Boolean = true,
    val totalNumberOfTags : List<TagModel> = emptyList(),
    val tagsSelectedForThisMemory : List<TagModel> = emptyList(),
    val tagTextFieldValue : String = "",
    val uriList : List<UriType> = emptyList(),
    val memoryForTimeStamp : Long? = null,
    val timeStamp: Long? = null,
    val memory : MemoryWithMediaModel? = null,
)


enum class CreationState{
    CREATE,
    UPDATE
}
