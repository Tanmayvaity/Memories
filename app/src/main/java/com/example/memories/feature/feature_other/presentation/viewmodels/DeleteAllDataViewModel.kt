package com.example.memories.feature.feature_other.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_other.data.repository.DeleteAllDataServiceImpl
import com.example.memories.feature.feature_other.domain.model.DeletionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllDataViewModel @Inject constructor(
    private val deleteAllDataService: DeleteAllDataServiceImpl
) : ViewModel() {

    private val _state = MutableStateFlow(DeleteDataState())
    val state = _state.asStateFlow()


    private val _event = MutableSharedFlow<String>(replay = 1)
    val event = _event.asSharedFlow()


    fun deleteAllData() {
        _state.update { it.copy(isDeleting = true) }
        deleteAllDataService.deleteAllData()
            .onEach { status ->
                when (status) {
                    is DeletionStatus.Error -> {
                        _state.update { it.copy(isDeleting = false) }
                        Log.e("DeleteAllDataViewModel", "deleteAllData: ${status.message}")
                        _event.emit("Failed to delete")
                    }

                    is DeletionStatus.InProgress -> {
                        _state.update { it.copy(isDeleting = true) }
                    }

                    DeletionStatus.Success -> {
                        Log.d("DeleteAllDataViewModel", "deleteAllData: Success")
                        _event.emit("Successfully deleted")
                        _state.update { it.copy(isDeleting = false) }
                    }

                    else -> {
                        _state.update {
                            it.copy(isDeleting = false)
                        }
                    }
                }

            }
            .launchIn(viewModelScope)

    }


}

data class DeleteDataState(
    val isDeleting: Boolean = false
)