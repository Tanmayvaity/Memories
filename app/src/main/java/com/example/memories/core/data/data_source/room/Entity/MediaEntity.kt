package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class MediaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("media_id")
    val mediaId: String ,
    @ColumnInfo("memory_id")
    val memoryId: String,
    val uri: String,
    val hidden: Boolean,
    val favourite: Boolean,
    @ColumnInfo("time_stamp")
    val timeStamp : Long,
)