package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class MemoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("memory_id")
    val memoryId : String,
    val title : String ,
    val content : String,
    val hidden : Boolean,
    val favourite : Boolean,
    @ColumnInfo("time_stamp")
    val timeStamp : Long,
    val longitude : Long?,
    val latitude : Long?,
    @ColumnInfo("memory_for_time_stamp")
    val memoryForTimeStamp : Long
)