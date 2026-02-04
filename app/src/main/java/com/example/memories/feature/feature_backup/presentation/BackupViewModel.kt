package com.example.memories.feature.feature_backup.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    @ApplicationContext val context : Context
) : ViewModel() {
    
    companion object {
        private const val TAG = "BackupViewModel"
    }
    
    val state: StateFlow<BackupScreenState>
        field : MutableStateFlow<BackupScreenState> = MutableStateFlow(BackupScreenState())

    fun onEvent(event: BackupEvents) {
        when (event) {
            is BackupEvents.ChangeFrequencyType -> {
                state.update {
                    it.copy(
                        backupFrequencyState = event.frequencyType
                    )
                }
                Log.d(TAG, "onEvent: ChangeFrequencyType : ${state.value.backupFrequencyState} ")

            }

        }

    }
}