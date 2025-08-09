package com.example.memories.feature.feature_feed.presentation.feed

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.usecase.FeedUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val feedUseCases: FeedUseCases
) : ViewModel() {

//    init {
//        onEvent(FeedEvents.FetchFeed)
//    }
    private val _state = MutableStateFlow<FeedState>(FeedState())
    val state = _state.asStateFlow()

    fun onEvent(event : FeedEvents){
        when(event){
            FeedEvents.FetchFeed -> {
               viewModelScope.launch {
                   val memoryWithMediaList : List<MemoryWithMediaModel> = feedUseCases.getFeedUseCase()

                   val filterList = memoryWithMediaList.filter { it !in _state.value.memories }

                   if(filterList.isNotEmpty()){
                       _state.update {
                           it.copy(
                               memories = _state.value.memories + memoryWithMediaList
                           )
                       }
                   }


               }
            }

        }

    }

}