package com.example.memories.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResultTest {

    @Test
    fun getOrNull_returnsDataForSuccess() {
        assertEquals("value", Result.Success("value").getOrNull())
    }

    @Test
    fun getOrNull_returnsNullForSuccessWithNullData() {
        assertNull(Result.Success<String>(null).getOrNull())
    }

    @Test
    fun getOrNull_returnsNullForError() {
        assertNull(Result.Error(RuntimeException("boom")).getOrNull())
    }
}
