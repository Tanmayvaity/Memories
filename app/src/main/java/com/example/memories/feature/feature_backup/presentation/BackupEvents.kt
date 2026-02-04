package com.example.memories.feature.feature_backup.presentation

import com.example.memories.feature.feature_backup.domain.model.BackupFrequency

sealed class BackupEvents {
    data class ChangeFrequencyType(val frequencyType: BackupFrequency) : BackupEvents()
}