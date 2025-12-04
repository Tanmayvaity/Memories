package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("tag_id")
    val tagId: String ,
    val label : String,
)