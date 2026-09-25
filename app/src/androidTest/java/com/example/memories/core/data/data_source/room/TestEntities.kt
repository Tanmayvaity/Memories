package com.example.memories.core.data.data_source.room

import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.SyncStatus
import com.example.memories.core.domain.model.Type

/** Small builders so DAO tests stay readable. */
object TestEntities {

    fun memory(
        id: String,
        title: String = "Title $id",
        content: String = "content",
        hidden: Boolean = false,
        favourite: Boolean = false,
        timeStamp: Long = 1_000L,
        memoryForTimeStamp: Long = 1_000L,
        owner: String = LOCAL_OWNER,
        syncStatus: SyncStatus = SyncStatus.CREATE_SYNC_PENDING,
    ) = MemoryEntity(
        memoryId = id,
        title = title,
        content = content,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp,
        longitude = null,
        latitude = null,
        memoryForTimeStamp = memoryForTimeStamp,
        owner = owner,
        syncStatus = syncStatus,
    )

    fun media(
        id: String,
        memoryId: String,
        position: Int = 0,
        type: Type = Type.IMAGE_JPG,
        timeStamp: Long = 1_000L,
        owner: String = LOCAL_OWNER,
    ) = MediaEntity(
        mediaId = id,
        memoryId = memoryId,
        uri = "content://media/$id",
        hidden = false,
        favourite = false,
        timeStamp = timeStamp,
        longitude = null,
        latitude = null,
        position = position,
        type = type,
        owner = owner,
    )

    fun tag(id: String, label: String, owner: String = LOCAL_OWNER) =
        TagEntity(tagId = id, label = label, owner = owner)

    fun search(memoryId: String, timeStamp: Long) =
        SearchEntity(memoryId = memoryId, timeStamp = timeStamp)
}
