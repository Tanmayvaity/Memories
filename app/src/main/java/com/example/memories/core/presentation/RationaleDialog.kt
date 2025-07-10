package com.example.memories.core.presentation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
    iconColor : Color = Color.Black,
    iconContentDescription: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    containerColor: Color = Color.White,
    btnColor: Color = Color.Black
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
            )
        },
        text = {
            Text(
                text = body,

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