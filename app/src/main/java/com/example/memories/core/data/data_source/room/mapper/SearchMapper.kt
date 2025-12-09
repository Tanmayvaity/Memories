package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.SearchModel

fun SearchEntity.toDomain() : SearchModel {
    return SearchModel(
        memoryId = memoryId,
        timeStamp = timeStamp
    )
}
fun SearchModel.toEntity() : SearchEntity {

    if(timeStamp==null)throw NullPointerException("time stamp null")

    return SearchEntity(
       memoryId = memoryId,
        timeStamp = timeStamp
    )
}