package com.example.memories.core.domain.model

enum class RetentionUnit { DAYS, WEEKS, MONTHS }

/**
 * How long memories stay on this device once the account syncs them.
 * Only synced memories are ever eligible for removal.
 */
sealed interface LocalRetention {

    /** Local memories are never deleted. */
    data object Forever : LocalRetention

    /** Synced memories older than this period are removed from the device. */
    data class Keep(val amount: Int, val unit: RetentionUnit) : LocalRetention

    /** Nothing is kept beyond what's needed: memories stay only until they're synced. */
    data object Never : LocalRetention

    companion object {
        val OneMonth = Keep(1, RetentionUnit.MONTHS)
        val ThreeMonths = Keep(3, RetentionUnit.MONTHS)
        val SixMonths = Keep(6, RetentionUnit.MONTHS)
        val Presets: List<Keep> = listOf(OneMonth, ThreeMonths, SixMonths)

        /** Used until the user picks a value. */
        val Default: LocalRetention = ThreeMonths
    }
}
