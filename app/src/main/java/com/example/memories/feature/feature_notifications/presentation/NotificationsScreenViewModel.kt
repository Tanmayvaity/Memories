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
        repository.onThisDayNotificationAllowed,
        repository.reminderTime

    ) { all, reminder, onThisDay, reminderTime ->
        NotificationsScreenState(
            allNotificationsEnabled = all,
            reminderNotificationEnabled = reminder,
            onThisDayNotificationEnabled = onThisDay,
            reminderHour = reminderTime / 60,
            reminderMinute = reminderTime % 60
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotificationsScreenState()
        )


    fun onEvent(event: NotificationsEvents) {
        when (event) {
            is NotificationsEvents.SetAllNotifications -> {
                viewModelScope.launch {
                    notificationUseCase.setAllNotificationsUseCase(
                        event.enabled,
                        state.value.reminderHour,
                        state.value.reminderMinute
                    )
                }
            }

            is NotificationsEvents.SetReminderNotification -> {
                viewModelScope.launch {
                    notificationUseCase.setReminderNotificationUseCase(
                        event.enabled,
                        state.value.reminderHour,
                        state.value.reminderMinute
                    )
                }
            }

            is NotificationsEvents.SetOnThisDayNotification -> {
                viewModelScope.launch {
                    notificationUseCase.setOnThisDayNotificationUseCase(event.enabled)
                }
            }

            is NotificationsEvents.SetReminderTime -> {
                viewModelScope.launch {
                    notificationUseCase.setReminderTimeUseCase(event.hour, event.minute)
                }
            }
        }
    }


}


data class NotificationsScreenState(
    val allNotificationsEnabled: Boolean = false,
    val reminderNotificationEnabled: Boolean = false,
    val onThisDayNotificationEnabled: Boolean = false,
    val reminderHour: Int = 22,
    val reminderMinute: Int = 0,
)