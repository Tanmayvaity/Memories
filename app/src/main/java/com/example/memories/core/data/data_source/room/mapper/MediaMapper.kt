package com.example.memories.core.data.data_source.room.mapper

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.domain.model.MediaModel
import java.util.UUID


fun MediaEntity.toDomain() : MediaModel {
    return MediaModel(
        mediaId = mediaId,
        memoryId = memoryId,
        uri = uri,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp
    )
}
fun MediaModel.toEntity() : MediaEntity {
    return MediaEntity(
        mediaId = mediaId,
        memoryId = memoryId,
        uri = uri,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp
    )
}








