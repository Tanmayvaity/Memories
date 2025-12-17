package com.example.memories.feature.feature_memory.presentation.components

import android.R.attr.contentDescription
import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun ReminderPickerButton(
    modifier: Modifier = Modifier,
    onClick : () -> Unit,
    shape : Shape = RoundedCornerShape(10.dp),
    containerColor : Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor : Color = MaterialTheme.colorScheme.onPrimaryContainer,
    buttonText : String = "Button",
    buttonColor : Color = MaterialTheme.colorScheme.onPrimaryContainer,
    imageVector : ImageVector,
    vectorContentDescription : String,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Set a reminder"
            )
            Icon(
                imageVector = imageVector,
                tint = buttonColor,
                contentDescription = vectorContentDescription
            )
        }
    }
}

@Preview
@Composable
fun ReminderPickerButtonPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ReminderPickerButton(
            onClick = {},
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            vectorContentDescription = ""
        )
    }
}