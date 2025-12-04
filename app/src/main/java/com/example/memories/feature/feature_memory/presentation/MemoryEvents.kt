package com.example.memories.feature.feature_memory.presentation

import androidx.compose.ui.focus.FocusState
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType


sealed class MemoryEvents {
    data class TitleChanged(val value: String) : MemoryEvents()
    data class TitleFocusChanged(val focusState: FocusState) : MemoryEvents()
    data class ContentChanged(val value : String): MemoryEvents()
    data class ContentFocusChanged(val focusState : FocusState) : MemoryEvents()

    data class CreateMemory(
        val uriList : List<UriType>,
        val title : String,
        val content : String ,
        ): MemoryEvents()

    object FetchTags : MemoryEvents()

    data class AddTag(val tag : String) : MemoryEvents()

    data class UpdateTagsInTextField(val tag : TagModel) : MemoryEvents()
    data class RemoveTagsFromTextField(val tag : TagModel) : MemoryEvents()

    data class TagsTextFieldContentChanged(val value : String) : MemoryEvents()
}