package com.example.memories.feature.feature_onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.domain.usecase.InvokeNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val otherSettingsDatastore: OtherSettingsDatastore,
    private val invokeNotificationUseCase: InvokeNotificationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = otherSettingsDatastore.isOnboardingCompleted.first()
            _state.update {
                it.copy(
                    isLoading = false,
                    isOnboardingCompleted = completed
                )
            }
        }
    }

    fun onEvent(event: OnboardingEvents) {
        when (event) {
            is OnboardingEvents.NextPage -> {
                _state.update {
                    if (it.currentPage < it.totalPages - 1) {
                        it.copy(currentPage = it.currentPage + 1)
                    } else {
                        it
                    }
                }
            }

            is OnboardingEvents.PreviousPage -> {
                _state.update {
                    if (it.currentPage > 0) {
                        it.copy(currentPage = it.currentPage - 1)
                    } else {
                        it
                    }
                }
            }

            is OnboardingEvents.PageChanged -> {
                _state.update { it.copy(currentPage = event.page) }
            }

            is OnboardingEvents.SkipOnboarding -> {
                viewModelScope.launch {
                    otherSettingsDatastore.setOnboardingCompleted()
                    _state.update { it.copy(isOnboardingCompleted = true) }
                }
            }

            is OnboardingEvents.CompleteOnboarding -> {
                viewModelScope.launch {
                    otherSettingsDatastore.setOnboardingCompleted()
                    _state.update { it.copy(isOnboardingCompleted = true) }
                }
            }

            is OnboardingEvents.UpdateCameraPermission -> {
                _state.update { it.copy(cameraPermissionGranted = event.granted) }
            }

            is OnboardingEvents.UpdateNotificationPermission -> {
                _state.update { it.copy(notificationPermissionGranted = event.granted) }
            }

            is OnboardingEvents.UpdateAlarmPermission -> {
                _state.update { it.copy(alarmPermissionGranted = event.granted) }
            }

            OnboardingEvents.ScheduleReminderNotifications -> {
                viewModelScope.launch {
                    invokeNotificationUseCase()
                }
            }
        }
    }
}
