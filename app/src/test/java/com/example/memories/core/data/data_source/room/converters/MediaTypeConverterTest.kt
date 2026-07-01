package com.example.memories.core.data.data_source.room.converters

import com.example.memories.core.data.data_source.room.converters.MediaTypeConverter
import com.example.memories.core.domain.model.Type
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaTypeConverterTest {

    private val converter = MediaTypeConverter()

    @Test
    fun fromType_returnsMimeType() {
        assertEquals("image/jpeg", converter.fromType(Type.IMAGE_JPG))
        assertEquals("image/png", converter.fromType(Type.IMAGE_PNG))
        assertEquals("video/mp4", converter.fromType(Type.VIDEO_MP4))
        assertEquals("unknown", converter.fromType(Type.UNKNOWN_TYPE))
    }

    @Test
    fun toType_mapsKnownMimeTypes() {
        assertEquals(Type.IMAGE_JPG, converter.toType("image/jpeg"))
        assertEquals(Type.IMAGE_JPG, converter.toType("image/jpg"))
        assertEquals(Type.IMAGE_PNG, converter.toType("image/png"))
        assertEquals(Type.VIDEO_MP4, converter.toType("video/mp4"))
    }

    @Test
    fun toType_returnsUnknownForUnsupportedMime() {
        assertEquals(Type.UNKNOWN_TYPE, converter.toType("application/zip"))
        assertEquals(Type.UNKNOWN_TYPE, converter.toType("image/gif"))
    }

    @Test
    fun roundTrip_throughMimeStringIsStableForKnownTypes() {
        for (type in listOf(Type.IMAGE_JPG, Type.IMAGE_PNG, Type.VIDEO_MP4, Type.UNKNOWN_TYPE)) {
            assertEquals(type, converter.toType(converter.fromType(type)))
        }
    }
}
