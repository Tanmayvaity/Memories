package com.example.memories.core.data.data_source

import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.domain.model.RetentionUnit

/**
 * Stores [LocalRetention] as a single DataStore string: `FOREVER`, `NEVER` or `KEEP:<amount>:<unit>`.
 * A missing value (nothing chosen yet) is [LocalRetention.Default]. An unreadable value falls back to
 * [LocalRetention.Forever], the only choice that can never delete data, so corruption can't trigger removal.
 */
object LocalRetentionCodec {

    private const val FOREVER = "FOREVER"
    private const val NEVER = "NEVER"
    private const val KEEP = "KEEP"

    fun encode(retention: LocalRetention): String = when (retention) {
        LocalRetention.Forever -> FOREVER
        LocalRetention.Never -> NEVER
        is LocalRetention.Keep -> "$KEEP:${retention.amount}:${retention.unit.name}"
    }

    fun decode(value: String?): LocalRetention {
        when (value) {
            null -> return LocalRetention.Default
            FOREVER -> return LocalRetention.Forever
            NEVER -> return LocalRetention.Never
        }
        val parts = value.split(":")
        if (parts.size != 3 || parts[0] != KEEP) return LocalRetention.Forever
        val amount = parts[1].toIntOrNull()?.takeIf { it > 0 } ?: return LocalRetention.Forever
        val unit = RetentionUnit.entries.firstOrNull { it.name == parts[2] } ?: return LocalRetention.Forever
        return LocalRetention.Keep(amount, unit)
    }
}
