package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.Type
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MediaMapperTest {

    @Test
    fun mediaEntity_toDomain_copiesAllFields() {
        val entity = MediaEntity(
            mediaId = "med1",
            memoryId = "m1",
            uri = "content://x",
            hidden = true,
            favourite = true,
            timeStamp = 42L,
            longitude = 1L,
            latitude = 2L,
            position = 3,
            type = Type.VIDEO_MP4,
        )

        val model = entity.toDomain()

        assertEquals("med1", model.mediaId)
        assertEquals("m1", model.memoryId)
        assertEquals("content://x", model.uri)
        assertEquals(true, model.hidden)
        assertEquals(true, model.favourite)
        assertEquals(42L, model.timeStamp)
        assertEquals(3, model.position)
        assertEquals(Type.VIDEO_MP4, model.type)
    }

    @Test
    fun mediaModel_toEntity_copiesFieldsAndNullsLocation() {
        val model = MediaModel(
            mediaId = "med2",
            memoryId = "m2",
            uri = "content://y",
            hidden = false,
            favourite = true,
            timeStamp = 7L,
            position = 5,
            type = Type.IMAGE_PNG,
        )

        val entity = model.toEntity()

        assertEquals("med2", entity.mediaId)
        assertEquals("m2", entity.memoryId)
        assertEquals("content://y", entity.uri)
        assertEquals(false, entity.hidden)
        assertEquals(true, entity.favourite)
        assertEquals(7L, entity.timeStamp)
        assertEquals(5, entity.position)
        assertEquals(Type.IMAGE_PNG, entity.type)
        assertNull(entity.longitude)
        assertNull(entity.latitude)
    }
}
