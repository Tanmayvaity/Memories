package com.example.memories.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeTest {

    @Test
    fun fromMimeType_mapsKnownTypes() {
        assertEquals(Type.IMAGE_JPG, Type.fromMimeType("image/jpeg"))
        assertEquals(Type.IMAGE_JPG, Type.fromMimeType("image/jpg"))
        assertEquals(Type.IMAGE_PNG, Type.fromMimeType("image/png"))
        assertEquals(Type.VIDEO_MP4, Type.fromMimeType("video/mp4"))
    }

    @Test
    fun fromMimeType_returnsUnknownForUnsupported() {
        assertEquals(Type.UNKNOWN_TYPE, Type.fromMimeType("image/gif"))
        assertEquals(Type.UNKNOWN_TYPE, Type.fromMimeType("application/pdf"))
        assertEquals(Type.UNKNOWN_TYPE, Type.fromMimeType(""))
    }

    @Test
    fun isImageFile_trueForImageTypesOnly() {
        assertTrue(Type.IMAGE_JPG.isImageFile())
        assertTrue(Type.IMAGE_PNG.isImageFile())
        assertFalse(Type.VIDEO_MP4.isImageFile())
        assertFalse(Type.UNKNOWN_TYPE.isImageFile())
    }

    @Test
    fun isVideoFile_trueForVideoOnly() {
        assertTrue(Type.VIDEO_MP4.isVideoFile())
        assertFalse(Type.IMAGE_JPG.isVideoFile())
        assertFalse(Type.UNKNOWN_TYPE.isVideoFile())
    }

    @Test
    fun isJpgFile_trueForJpgOnly() {
        assertTrue(Type.IMAGE_JPG.isJpgFile())
        assertFalse(Type.IMAGE_PNG.isJpgFile())
        assertFalse(Type.VIDEO_MP4.isJpgFile())
    }

    @Test
    fun isUnknownType_trueForUnknownOnly() {
        assertTrue(Type.UNKNOWN_TYPE.isUnknownType())
        assertFalse(Type.IMAGE_JPG.isUnknownType())
    }

    @Test
    fun mimeType_propertyMatchesEnum() {
        assertEquals("image/jpeg", Type.IMAGE_JPG.mimeType)
        assertEquals("image/png", Type.IMAGE_PNG.mimeType)
        assertEquals("video/mp4", Type.VIDEO_MP4.mimeType)
        assertEquals("unknown", Type.UNKNOWN_TYPE.mimeType)
    }
}
