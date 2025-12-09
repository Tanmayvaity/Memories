package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MemoryEntity::class,
            parentColumns = ["memory_id"],
            childColumns = ["memory_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SearchEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("memory_id")
    val memoryId : String,
    @ColumnInfo("time_stamp")
    val timeStamp: Long,
)
