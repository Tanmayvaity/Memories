package com.example.memories.feature.feature_notifications.domain.usecase

import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.domain.repository.MemoryNotificationScheduler
import com.example.memories.feature.feature_notifications.domain.repository.NotificationRepository

class SetOnThisDayNotificationUseCase (
    private val repository: NotificationRepository,
    private val scheduler: MemoryNotificationScheduler
) {
    suspend operator fun invoke(enabled : Boolean) {
        repository.enableOnThisDayNotification(enabled)

        // check if notification permission has been granted
        // if yes schedule a work manager work for setting up work when it is idle
        // the work manager will check whether to show the notifications as a double check
        // and kill itself

        if(enabled){
            scheduler.scheduleWork()
        }else{
            scheduler.cancelWork()
        }

    }
}