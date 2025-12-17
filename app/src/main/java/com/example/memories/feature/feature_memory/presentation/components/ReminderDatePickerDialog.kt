package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: (Long?) -> Unit = {},
    datePickerState: DatePickerState = rememberDatePickerState()
) {
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                }
            ) {
                Text(
                    text = "Confirm",
//                    color = Color.Black
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(
                    text = "Cancel",
                )
            }
        }


    ) {
        DatePicker(
            state = datePickerState,
//            title = {
//                Text(
//                    text = "Select Date",
//                    modifier = Modifier.padding(10.dp)
//                )
//            }

        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialogPreview() {
    MemoriesTheme {
        ReminderDatePickerDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}