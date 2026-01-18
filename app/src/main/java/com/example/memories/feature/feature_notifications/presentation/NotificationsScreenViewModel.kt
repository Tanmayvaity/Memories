package com.example.memories.feature.feature_notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository
import com.example.memories.feature.feature_notifications.domain.usecase.NotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
    private val notificationUseCase: NotificationUseCase,
    private val repository: NotificationRepository
) : ViewModel() {

    val state = combine(
        repository.allNotificationAllowed,
        repository.reminderNotificationAllowed,
        repository.onThisDayNotificationAllowed

    ){ all, reminder, onThisDay ->
        NotificationsScreenState(
            allNotificationsEnabled = all,
            reminderNotificationEnabled = reminder,
            onThisDayNotificationEnabled = onThisDay
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationsScreenState()
        )



    fun onEvent(event : NotificationsEvents){
        when(event){
            is NotificationsEvents.SetAllNotifications -> {
                viewModelScope.launch {
                    notificationUseCase.setAllNotificationsUseCase(event.enabled)
                }
            }
            is NotificationsEvents.SetReminderNotification -> {
                viewModelScope.launch {
                    notificationUseCase.setReminderNotificationUseCase(event.enabled)
                }
            }
            is NotificationsEvents.SetOnThisDayNotification -> {
                viewModelScope.launch {
                    notificationUseCase.setOnThisDayNotificationUseCase(event.enabled)
                }
            }

        }
    }
}


data class NotificationsScreenState(
    val allNotificationsEnabled : Boolean = false,
    val reminderNotificationEnabled : Boolean = false,
    val onThisDayNotificationEnabled : Boolean = false
)