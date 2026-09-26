package com.example.memories.core.data.data_source

import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.domain.model.RetentionUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalRetentionCodecTest {

    @Test
    fun roundTrip_everyKindOfRetention() {
        listOf(
            LocalRetention.Forever,
            LocalRetention.Never,
            LocalRetention.OneMonth,
            LocalRetention.ThreeMonths,
            LocalRetention.SixMonths,
            LocalRetention.Keep(45, RetentionUnit.DAYS),
            LocalRetention.Keep(2, RetentionUnit.WEEKS),
        ).forEach { retention ->
            assertEquals(retention, LocalRetentionCodec.decode(LocalRetentionCodec.encode(retention)))
        }
    }

    @Test
    fun encode_usesStableStrings() {
        assertEquals("FOREVER", LocalRetentionCodec.encode(LocalRetention.Forever))
        assertEquals("NEVER", LocalRetentionCodec.encode(LocalRetention.Never))
        assertEquals("KEEP:3:MONTHS", LocalRetentionCodec.encode(LocalRetention.ThreeMonths))
    }

    @Test
    fun decode_missingValue_isTheDefault_threeMonths() {
        assertEquals(LocalRetention.Default, LocalRetentionCodec.decode(null))
        assertEquals(LocalRetention.ThreeMonths, LocalRetention.Default)
    }

    @Test
    fun decode_unreadableValue_fallsBackToForever_soNothingIsDeleted() {
        listOf("", "garbage", "KEEP", "KEEP:abc:DAYS", "KEEP:0:DAYS", "KEEP:-3:DAYS", "KEEP:5:YEARS", "KEEP:5:DAYS:x")
            .forEach { assertEquals(it, LocalRetention.Forever, LocalRetentionCodec.decode(it)) }
    }
}
