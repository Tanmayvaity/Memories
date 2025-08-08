package com.example.memories.feature.feature_memory.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_memory.domain.usecase.MemoryCreateUseCase
import com.example.memories.feature.feature_memory.domain.usecase.MemoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel @Inject constructor(
    val memoryUseCase : MemoryUseCase
): ViewModel() {

    companion object{
        private const val TAG = "MemoryViewModel"

    }

    private val _errorFlow = Channel<String>()
    val errorFlow = _errorFlow.receiveAsFlow()

    private val _successFlow = Channel<String>()
    val successFlow = _successFlow.receiveAsFlow()


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

            is MemoryEvents.CreateMemory -> {
                viewModelScope.launch {
                    val result = memoryUseCase.createMemoryUseCase(
                        uriList = event.uriList,
                        title = event.title,
                        content = event.content
                    )
                    when(result){
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: ${result.error.message}")
                            _errorFlow.send(result.error.message.toString())
                        }
                        is Result.Success<String> -> {
                            _successFlow.send(result.data.toString())
                        }
                    }

                }
            }

        }
    }
}