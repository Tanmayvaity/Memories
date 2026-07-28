package com.example.memories.feature.feature_firebase.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.feature.feature_firebase.domain.model.ProviderLinkAction
import com.example.memories.feature.feature_firebase.domain.model.SocialProvider
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun LinkedAccountsCard(
    connectedProviders: Set<SocialProvider>,
    onProviderAction: (SocialProvider, ProviderLinkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            SocialProvider.entries.forEachIndexed { index, provider ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                val isConnected = provider in connectedProviders
                // The account needs at least one way back in, so the last one can't be dropped.
                val isOnlyMethod = isConnected && connectedProviders.size == 1
                CustomSettingRow(
                    modifier = Modifier.padding(12.dp),
                    heading = provider.displayName,
                    // The account email is the same for every row, so the section footer carries it.
                    // Kept short so it can't wrap onto a second line; the greyed-out Disconnect
                    // already implies this one is connected.
                    content = when {
                        isOnlyMethod -> "Only sign-in method"
                        isConnected -> "Connected"
                        else -> "Not connected"
                    },
                    showContentAtEnd = true,
                    drawableRes = null,
                    leading = {
                        Icon(
                            painter = painterResource(provider.iconRes),
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = if (provider.tintWithTheme) MaterialTheme.colorScheme.onSurface
                            else Color.Unspecified
                        )
                    },
                    endContent = {
                        if (isConnected) {
                            DisconnectButton(
                                enabled = !isOnlyMethod,
                                onClick = {
                                    onProviderAction(provider, ProviderLinkAction.DISCONNECT)
                                }
                            )
                        } else {
                            ConnectButton(
                                onClick = { onProviderAction(provider, ProviderLinkAction.CONNECT) }
                            )
                        }
                    },
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun ConnectButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_link),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = ProviderLinkAction.CONNECT.confirmLabel,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun DisconnectButton(enabled: Boolean, onClick: () -> Unit) {
    TextButton(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(
            text = ProviderLinkAction.DISCONNECT.confirmLabel,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkedAccountsCardSingleProviderPreview() {
    MemoriesTheme {
        LinkedAccountsCard(
            connectedProviders = setOf(SocialProvider.GOOGLE),
            onProviderAction = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LinkedAccountsCardMultipleProvidersPreview() {
    MemoriesTheme {
        LinkedAccountsCard(
            connectedProviders = setOf(SocialProvider.GOOGLE, SocialProvider.GITHUB),
            onProviderAction = { _, _ -> }
        )
    }
}
