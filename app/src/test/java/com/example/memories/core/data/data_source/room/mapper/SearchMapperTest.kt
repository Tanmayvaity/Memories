package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.SearchModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchMapperTest {

    @Test
    fun searchEntity_toDomain_copiesFields() {
        val model = SearchEntity(memoryId = "m1", timeStamp = 99L).toDomain()

        assertEquals("m1", model.memoryId)
        assertEquals(99L, model.timeStamp)
    }

    @Test
    fun searchModel_toEntity_copiesFieldsWhenTimeStampPresent() {
        val entity = SearchModel(memoryId = "m1", timeStamp = 50L).toEntity()

        assertEquals("m1", entity.memoryId)
        assertEquals(50L, entity.timeStamp)
    }

    @Test(expected = NullPointerException::class)
    fun searchModel_toEntity_throwsWhenTimeStampNull() {
        SearchModel(memoryId = "m1").toEntity()
    }
}
