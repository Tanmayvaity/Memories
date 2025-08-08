package com.example.memories.feature.feature_memory.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.memories.ui.theme.MemoriesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    state: DatePickerState = rememberDatePickerState()
) {
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
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
            state = state
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialogPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ReminderDatePickerDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}