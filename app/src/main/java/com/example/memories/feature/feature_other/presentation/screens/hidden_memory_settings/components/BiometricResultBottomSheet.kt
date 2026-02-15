package com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_other.presentation.BiometricResult

data class BiometricSheetContent(
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconTint: Color,
    val title: String,
    val description: String,
    val primaryButtonText: String,
    val primaryButtonAction: BiometricSheetAction,
    val secondaryButtonText: String? = null,
    val secondaryButtonAction: BiometricSheetAction? = null,
    val tertiaryButtonText: String? = null,
    val tertiaryButtonAction: BiometricSheetAction? = null
)

enum class BiometricSheetAction {
    OPEN_SETTINGS,
    USE_CUSTOM_PIN,
    RETRY,
    DISMISS
}

fun BiometricResult.toSheetContent(): BiometricSheetContent {
    return when (this) {
        is BiometricResult.AuthenticationNotSet -> BiometricSheetContent(
            icon = Icons.Default.Warning,
            iconBackgroundColor = Color(0xFFFFE0D0),
            iconTint = Color(0xFF8B2500),
            title = "Security Method Not Found",
            description = "Your device doesn't have a PIN or Pattern configured. Please set one up in your system settings to use it as a lock method for your hidden memories.",
            primaryButtonText = "Open System Settings",
            primaryButtonAction = BiometricSheetAction.OPEN_SETTINGS,
            secondaryButtonText = "Use Custom PIN",
            secondaryButtonAction = BiometricSheetAction.USE_CUSTOM_PIN,
            tertiaryButtonText = "Cancel",
            tertiaryButtonAction = BiometricSheetAction.DISMISS
        )

        is BiometricResult.HardwareUnavailable -> BiometricSheetContent(
            icon = Icons.Default.Warning,
            iconBackgroundColor = Color(0xFFFFE0D0),
            iconTint = Color(0xFF8B2500),
            title = "Biometric Hardware Unavailable",
            description = "Your device doesn't support biometric authentication. You can use a custom PIN to protect your hidden memories instead.",
            primaryButtonText = "Use Custom PIN",
            primaryButtonAction = BiometricSheetAction.USE_CUSTOM_PIN,
            secondaryButtonText = "Cancel",
            secondaryButtonAction = BiometricSheetAction.DISMISS
        )

        is BiometricResult.FeatureUnavailable -> BiometricSheetContent(
            icon = Icons.Default.Warning,
            iconBackgroundColor = Color(0xFFFFE0D0),
            iconTint = Color(0xFF8B2500),
            title = "Biometric Feature Unavailable",
            description = "Biometric authentication is currently unavailable on your device. You can set up a custom PIN or configure biometrics in your system settings.",
            primaryButtonText = "Open System Settings",
            primaryButtonAction = BiometricSheetAction.OPEN_SETTINGS,
            secondaryButtonText = "Use Custom PIN",
            secondaryButtonAction = BiometricSheetAction.USE_CUSTOM_PIN,
            tertiaryButtonText = "Cancel",
            tertiaryButtonAction = BiometricSheetAction.DISMISS
        )

        is BiometricResult.AuthenticationFailed -> BiometricSheetContent(
            icon = Icons.Default.Close,
            iconBackgroundColor = Color(0xFFFFD6D6),
            iconTint = Color(0xFFB71C1C),
            title = "Authentication Failed",
            description = "The biometric authentication was not recognized. Please try again or use a custom PIN instead.",
            primaryButtonText = "Try Again",
            primaryButtonAction = BiometricSheetAction.RETRY,
            secondaryButtonText = "Use Custom PIN",
            secondaryButtonAction = BiometricSheetAction.USE_CUSTOM_PIN,
            tertiaryButtonText = "Cancel",
            tertiaryButtonAction = BiometricSheetAction.DISMISS
        )

        is BiometricResult.AuthenticationError -> BiometricSheetContent(
            icon = Icons.Default.Warning,
            iconBackgroundColor = Color(0xFFFFE0D0),
            iconTint = Color(0xFF8B2500),
            title = "Authentication Error",
            description = error,
            primaryButtonText = "Try Again",
            primaryButtonAction = BiometricSheetAction.RETRY,
            secondaryButtonText = "Use Custom PIN",
            secondaryButtonAction = BiometricSheetAction.USE_CUSTOM_PIN,
            tertiaryButtonText = "Cancel",
            tertiaryButtonAction = BiometricSheetAction.DISMISS
        )

        is BiometricResult.AuthenticationSuccess -> BiometricSheetContent(
            icon = Icons.Default.Check,
            iconBackgroundColor = Color(0xFFD0F5D0),
            iconTint = Color(0xFF1B5E20),
            title = "Authentication Successful",
            description = "Your identity has been verified. You can now access your hidden memories.",
            primaryButtonText = "Done",
            primaryButtonAction = BiometricSheetAction.DISMISS
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricResultBottomSheet(
    result: BiometricResult,
    onAction: (BiometricSheetAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val content = remember(result) { result.toSheetContent() }

    ModalBottomSheet(
        onDismissRequest = { onAction(BiometricSheetAction.DISMISS) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(content.iconBackgroundColor)
            ) {
                Icon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = content.iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Button
            Button(
                onClick = { onAction(content.primaryButtonAction) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = content.primaryButtonText,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Secondary Button
            content.secondaryButtonText?.let { text ->
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { onAction(content.secondaryButtonAction!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Tertiary Button
            content.tertiaryButtonText?.let { text ->
                TextButton(
                    onClick = { onAction(content.tertiaryButtonAction!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun BiometricResultBottomSheetPreview() {
    BiometricResultBottomSheet(
        result = BiometricResult.AuthenticationSuccess,
        onAction = {}
    )
}