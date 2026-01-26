package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MemoryEntity::class,
            parentColumns = ["memory_id"],
            childColumns = ["memory_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("memory_id")
    ]
)
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
    val longitude : Long?,
    val latitude : Long?,
    val position : Int = 0
)