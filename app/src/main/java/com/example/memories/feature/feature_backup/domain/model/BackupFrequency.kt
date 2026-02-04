package com.example.memories.feature.feature_backup.domain.model

enum class BackupFrequency(
    val displayName: String,
    val subHeading : String,
    val intervalHours: Long
) {
    DAILY("Daily","Every 24 hours", 24),
    WEEKLY("Weekly","Every 7 days", 168),
    MONTHLY("Monthly","Every 30 days", 720),
}