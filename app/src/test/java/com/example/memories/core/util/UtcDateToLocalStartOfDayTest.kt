package com.example.memories.core.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class UtcDateToLocalStartOfDayTest {

    private val originalZone = TimeZone.getDefault()

    @After
    fun tearDown() {
        TimeZone.setDefault(originalZone)
    }

    @Test
    fun aheadOfUtc_keepsDateAndDropsOffset() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"))

        val local = SEP_20_UTC_MIDNIGHT.utcDateToLocalStartOfDay()

        assertLocalMidnight(local, 2026, Calendar.SEPTEMBER, 20)
        assertEquals(SEP_20_UTC_MIDNIGHT - IST_OFFSET_MS, local)
    }

    @Test
    fun behindUtc_doesNotShiftToPreviousDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))

        assertLocalMidnight(SEP_20_UTC_MIDNIGHT.utcDateToLocalStartOfDay(), 2026, Calendar.SEPTEMBER, 20)
    }

    @Test
    fun utc_isUnchanged() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        assertEquals(SEP_20_UTC_MIDNIGHT, SEP_20_UTC_MIDNIGHT.utcDateToLocalStartOfDay())
    }

    @Test
    fun formatTime_showsPickedDateInAnyZone() {
        for (zone in listOf("Asia/Kolkata", "America/New_York", "Pacific/Auckland", "UTC")) {
            TimeZone.setDefault(TimeZone.getTimeZone(zone))

            assertEquals(zone, "20/09/2026", SEP_20_UTC_MIDNIGHT.utcDateToLocalStartOfDay().formatTime("dd/MM/yyyy"))
        }
    }

    private fun assertLocalMidnight(millis: Long, year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        assertEquals(year, cal.get(Calendar.YEAR))
        assertEquals(month, cal.get(Calendar.MONTH))
        assertEquals(day, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
    }

    private companion object {
        /** What Material3 DatePicker reports for 20 Sep 2026. */
        const val SEP_20_UTC_MIDNIGHT = 1_789_862_400_000L
        const val IST_OFFSET_MS = (5 * 60 + 30) * 60 * 1000L
    }
}
