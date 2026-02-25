package com.example.memories.feature.feature_other.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    onDismiss: () -> Unit,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    footerText: String? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            icon()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onPrimaryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = primaryButtonText,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (secondaryButtonText != null) {
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { onSecondaryClick?.invoke() }) {
                    Text(
                        text = secondaryButtonText,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (footerText != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = footerText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}