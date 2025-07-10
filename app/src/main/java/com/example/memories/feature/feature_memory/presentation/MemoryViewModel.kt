package com.example.memories.feature.feature_memory.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MemoryViewModel : ViewModel() {

    private val _memoryState = MutableStateFlow<MemoryState>(MemoryState())
    val memoryState = _memoryState.asStateFlow()


    fun onEvent(event: MemoryEvents) {
        when (event) {
            is MemoryEvents.TitleChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        title = event.value,
//                        titleHintContent = ""
                    )
                }
            }

            is MemoryEvents.TitleFocusChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        isTitleHintVisible = !event.focusState.isFocused && _memoryState.value.title.isBlank(),
                    )
                }
            }

            is MemoryEvents.ContentChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        content = event.value
                    )
                }
            }
            is MemoryEvents.ContentFocusChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        isContentHintVisible = !event.focusState.isFocused && _memoryState.value.content.isBlank(),
                    )
                }
            }
        }
    }
}