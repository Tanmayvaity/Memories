package com.example.memories.feature.feature_backup.presentation

import com.example.memories.feature.feature_backup.domain.model.BackupFrequency

data class BackupScreenState(
    val backupFrequencyState : BackupFrequency = BackupFrequency.DAILY
)