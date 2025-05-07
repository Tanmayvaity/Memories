package com.example.memories.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun CameraPermissionRejectionDialog(
    title : String,
    text : String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Camera Permission Rejected")
        },
        text = {
            Text(text = "You won't be able to use camera features without this permission")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Ok")
            }
        },

        )
}

@Composable
fun CameraRationaleDialog(
    title : String,
    text : String,
    onDismissRequest: () -> Unit,
    onConfirm : () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Settings")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}