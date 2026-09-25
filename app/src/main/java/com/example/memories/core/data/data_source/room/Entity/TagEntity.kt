package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.SyncStatus
import java.util.UUID

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("tag_id")
    val tagId: String ,
    val label : String,
    @ColumnInfo(name = "sync_status", defaultValue = "'CREATE_SYNC_PENDING'")
    val syncStatus: SyncStatus = SyncStatus.CREATE_SYNC_PENDING,
    @ColumnInfo(name = "owner", defaultValue = "'$LOCAL_OWNER'")
    val owner: String = LOCAL_OWNER,
)