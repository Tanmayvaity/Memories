package com.example.memories.core.data.data_source.room.converters

import androidx.room.TypeConverter
import com.example.memories.core.domain.model.SyncStatus


class SyncStatusConverter {

    @TypeConverter
    fun fromSyncStatus(syncStatus: SyncStatus): String {
        return syncStatus.name
    }

    @TypeConverter
    fun toSyncStatus(name: String): SyncStatus {
        return SyncStatus.fromName(name)
    }
}
