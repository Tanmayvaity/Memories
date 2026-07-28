package com.example.memories.feature.feature_firebase.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_firebase.domain.model.ProviderLinkAction
import com.example.memories.feature.feature_firebase.domain.model.SocialProvider
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkProviderSheet(
    provider: SocialProvider,
    action: ProviderLinkAction,
    modifier: Modifier = Modifier,
    email: String? = null,
    isLoading: Boolean = false,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val account = email ?: "your account"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (action.isDestructive) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(provider.iconRes),
                    contentDescription = null,
                    tint = when {
                        !provider.tintWithTheme -> Color.Unspecified
                        action.isDestructive -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${action.confirmLabel} ${provider.displayName}?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (action) {
                    ProviderLinkAction.CONNECT ->
                        "You'll be able to sign in to $account with ${provider.displayName} as " +
                                "well. Either way lands on the same memories."

                    ProviderLinkAction.DISCONNECT ->
                        "${provider.displayName} will no longer sign you in to $account. Your " +
                                "other sign-in methods keep working, and you can connect it again " +
                                "later."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = if (action.isDestructive) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                } else {
                    ButtonDefaults.buttonColors()
                },
                onClick = onConfirm
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (action == ProviderLinkAction.CONNECT) {
                            Icon(
                                painter = painterResource(provider.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (provider.tintWithTheme) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    Color.Unspecified
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text(
                            text = "${action.confirmLabel} ${provider.displayName}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LinkProviderSheetConnectPreview() {
    MemoriesTheme {
        LinkProviderSheet(
            provider = SocialProvider.GITHUB,
            action = ProviderLinkAction.CONNECT,
            email = "john.doe@gmail.com",
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LinkProviderSheetDisconnectPreview() {
    MemoriesTheme {
        LinkProviderSheet(
            provider = SocialProvider.GOOGLE,
            action = ProviderLinkAction.DISCONNECT,
            email = "john.doe@gmail.com",
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}
