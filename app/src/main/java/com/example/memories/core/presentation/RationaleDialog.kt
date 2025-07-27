package com.example.memories.core.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R

@Preview
@Composable
fun RationaleDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    body: String = "",
    icon: Int? = null,
    iconColor : Color = MaterialTheme.colorScheme.primary,
    iconContentDescription: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    btnColor: Color = MaterialTheme.colorScheme.primary
) {
    AlertDialog(
        containerColor = containerColor,
        iconContentColor = iconColor,

        icon = {
            icon?.let {
                Icon(
                    modifier = modifier.size(32.dp),
                    painter = painterResource(icon),
                    contentDescription = iconContentDescription
                )
            }

        },
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )

        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(containerColor = btnColor)
            ) {
                Text(
                    text = stringResource(R.string.settings)
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                }

            ) {
                Text(
                    text = stringResource(R.string.dismiss),
                    color = btnColor
                )
            }
        }
    )
}