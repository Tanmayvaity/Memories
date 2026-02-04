package com.example.memories.feature.feature_backup.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_backup.domain.model.BackupFrequency
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupFrequencySheet(
    modifier: Modifier = Modifier,
    onApplyBackupFrequency : (BackupFrequency) -> Unit = {},
    defaultFrequency: BackupFrequency = BackupFrequency.DAILY,
    onDismiss : () -> Unit = {},
    sheetState : SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedBackupFrequency by rememberSaveable { mutableStateOf(defaultFrequency) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Backup Frequency",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            BackupFrequency.entries.forEachIndexed { index, backupFrequency ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable(
                            onClick = {
                                selectedBackupFrequency = backupFrequency
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if(selectedBackupFrequency == backupFrequency) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = backupFrequency.displayName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = backupFrequency.subHeading,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        RadioButton(
                            selected = index == selectedBackupFrequency.ordinal,
                            onClick = {
//                                onClick(backupFrequency)
                                selectedBackupFrequency = backupFrequency
                            }

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                ,
                onClick = {
                    onApplyBackupFrequency(selectedBackupFrequency)
                },
            ) {
                Text(
                    text = "Apply",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun BackupFrequencySheetPreview() {
    MemoriesTheme {
        BackupFrequencySheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}