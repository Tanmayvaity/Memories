package com.example.memories.core.presentation.components

import android.R.attr.dialogTitle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.memories.R
import com.example.memories.feature.feature_feed.presentation.feed.FeedEvents
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun GeneralAlertDialog(
    modifier: Modifier = Modifier,
    icon : @Composable () -> Unit = {},
    title : String = "",
    text : String = "",
    onDismiss : () -> Unit = {},
    onConfirm :() -> Unit = {},
    confirmButton : @Composable () -> Unit = {},
    dismissButton : @Composable () -> Unit = {},
    containerColor : Color = MaterialTheme.colorScheme.surface,
) {
    AlertDialog(
        icon = {
            icon()
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            confirmButton()
        },
        dismissButton = {
            dismissButton()
        }
    )
}

@PreviewLightDark
@Preview
@Composable
fun GeneralAlertDialogPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        GeneralAlertDialog(
            title = "Delete Memory Alert",
            text = "Are you sure you want to delete this memory",
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    onClick = {
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {

                    }

                ) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}
