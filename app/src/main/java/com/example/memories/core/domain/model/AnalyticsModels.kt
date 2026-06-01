package com.example.memories.core.domain.model

/**
 * Per-day aggregate of journaling activity (one row per active day). [day] is a local
 * "yyyy-MM-dd" string produced by the DB; [words] is an approximate, space-based word count.
 */
data class DailyStat(
    val day: String,
    val count: Int,
    val words: Int,
)

/**
 * Mutually-exclusive memory classification: a memory is [video] if it has any video, else [photo]
 * if it has any image, else [textOnly] (no media). The three sum to the total memory count.
 */
data class MediaBreakdown(
    val textOnly: Int,
    val photo: Int,
    val video: Int,
)
