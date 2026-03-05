package com.example.memories.feature.feature_feed.presentation.hidden

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.util.query
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.UiState
import com.example.memories.feature.feature_feed.domain.usecase.hidden_usecase.GetHiddenFeedUseCase
import com.example.memories.feature.feature_feed.domain.usecase.hidden_usecase.HiddenUseCase
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction
import com.example.memories.feature.feature_feed.presentation.common.MemoryActionHandler
import com.example.memories.feature.feature_feed.presentation.feed_detail.MemoryDetailEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HiddenMemoryViewModel @Inject constructor(
    val hiddenUseCase: HiddenUseCase,
    val memoryActionHandler: MemoryActionHandler
) : ViewModel() {
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val memories: Flow<PagingData<MemoryWithMediaModel>> = _inputText
        .debounce(300)
        .flatMapLatest { query ->
            hiddenUseCase.getHiddenFeedUseCase(query)
        }
        .cachedIn(viewModelScope)

    fun onEvent(event : HiddenMemoryEvents){
        when(event){
            is HiddenMemoryEvents.InputTextChange -> {
                _inputText.update { event.text }
            }

            is HiddenMemoryEvents.Action -> {
                viewModelScope.launch {
                    memoryActionHandler.handle(action = event.action)
                }
            }

        }
    }


}



sealed class HiddenMemoryEvents{
    data class InputTextChange(val text : String) : HiddenMemoryEvents()
    data class Action(val action: MemoryAction) : HiddenMemoryEvents()
}


