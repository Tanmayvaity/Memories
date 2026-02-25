package com.example.memories.feature.feature_other.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp



@Composable
fun LockMethodRequiredBottomSheet(
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit,
) {
    InfoBottomSheet(
        onDismiss = onDismiss,
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Lock",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = "Lock Method Required",
        subtitle = "Please check whether you have set up a valid LockMethod to secure your hidden memories.",
        primaryButtonText = "Go to Settings",
        onPrimaryClick = onGoToSettings,
        secondaryButtonText = "Cancel",
        onSecondaryClick = onDismiss
    )
}