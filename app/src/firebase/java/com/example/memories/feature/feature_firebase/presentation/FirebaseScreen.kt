package com.example.memories.feature.feature_firebase.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_firebase.domain.model.ProviderLinkAction
import com.example.memories.feature.feature_firebase.domain.model.SocialProvider
import com.example.memories.feature.feature_firebase.presentation.components.AuthSheet
import com.example.memories.feature.feature_firebase.presentation.components.LinkProviderSheet
import com.example.memories.feature.feature_firebase.presentation.components.LinkedAccountsCard
import com.example.memories.feature.feature_firebase.presentation.components.LogoutSheet
import com.example.memories.feature.feature_firebase.presentation.components.SyncSettingRow
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun FirebaseRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    FirebaseScreen(
        modifier = modifier,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    // Auth state — including which providers are linked — is still local scaffolding. Swap for a
    // ViewModel once the Firebase SDK is wired.
    var isSignedIn by rememberSaveable { mutableStateOf(true) }
    var showLogoutSheet by rememberSaveable { mutableStateOf(false) }
    var showAuthSheet by rememberSaveable { mutableStateOf(false) }
    var connectedProviders by rememberSaveable(stateSaver = ConnectedProvidersSaver) {
        mutableStateOf(setOf(SocialProvider.GOOGLE))
    }
    var pendingLink by rememberSaveable(stateSaver = PendingLinkSaver) {
        mutableStateOf<PendingLink?>(null)
    }

    if (showLogoutSheet) {
        LogoutSheet(
            email = SIGNED_IN_EMAIL,
            onConfirmLogout = {
                showLogoutSheet = false
                isSignedIn = false
            },
            onDismiss = { showLogoutSheet = false }
        )
    }

    if (showAuthSheet) {
        AuthSheet(
            onSubmit = { _, _, _ ->
                showAuthSheet = false
                isSignedIn = true
            },
            onSocialSignIn = {
                showAuthSheet = false
                isSignedIn = true
            },
            onForgotPassword = { /* TODO */ },
            onDismiss = { showAuthSheet = false }
        )
    }

    pendingLink?.let { pending ->
        LinkProviderSheet(
            provider = pending.provider,
            action = pending.action,
            email = SIGNED_IN_EMAIL,
            onConfirm = {
                // TODO: link/unlink the credential once Firebase Auth is wired.
                connectedProviders = when (pending.action) {
                    ProviderLinkAction.CONNECT -> connectedProviders + pending.provider
                    ProviderLinkAction.DISCONNECT -> connectedProviders - pending.provider
                }
                pendingLink = null
            },
            onDismiss = { pendingLink = null }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = "Remote Sync",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showDivider = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Account card
            AnimatedContent(targetState = isSignedIn) { isSignedIn ->
                when(isSignedIn){
                    true -> {
                        AccountCard(
                            connectedProviders = connectedProviders,
                            onSignOutClick = { showLogoutSheet = true }
                        )
                    }
                    false -> {
                        SignedOutCard(onSignInClick = { showAuthSheet = true })
                    }
                }

            }

            // Sign-in methods section
            AnimatedVisibility(visible = isSignedIn) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "SIGN-IN METHODS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinkedAccountsCard(
                        connectedProviders = connectedProviders,
                        onProviderAction = { provider, action ->
                            pendingLink = PendingLink(provider = provider, action = action)
                        }
                    )
                    Text(
                        text = if (connectedProviders.size > 1) {
                            "Signing in with any connected provider takes you to $SIGNED_IN_EMAIL."
                        } else {
                            "Connect another provider to sign in to $SIGNED_IN_EMAIL any way you " +
                                    "like. You'll always need at least one."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            // Sync section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SYNC",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SyncStatusCard()

                SettingCard(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        SyncSettingRow(
                            title = "Sync over cellular",
                            subtitle = "Sync when Wi-Fi is unavailable",
                            trailing = {
                                Switch(checked = false, onCheckedChange = {})
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        SyncSettingRow(
                            title = "Sync hidden memories",
                            subtitle = "Vaulted items stay on this device by default",
                            trailing = {
                                Switch(checked = false, onCheckedChange = {})
                            }
                        )
                    }
                }

                Text(
                    text = "Sync is optional. Your memories are always saved on this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            // Manage backups
            ManageBackupsCard(onClick = { /* TODO */ })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private const val SIGNED_IN_NAME = "John Doe"
private const val SIGNED_IN_EMAIL = "john.doe@gmail.com"

private data class PendingLink(
    val provider: SocialProvider,
    val action: ProviderLinkAction,
)

// Enums and sets aren't saveable out of the box, so round-trip them through their names.
private val ConnectedProvidersSaver = listSaver<Set<SocialProvider>, String>(
    save = { providers -> providers.map { it.name } },
    restore = { names -> names.map { SocialProvider.valueOf(it) }.toSet() }
)

private val PendingLinkSaver = listSaver<PendingLink?, String>(
    save = { pending -> pending?.let { listOf(it.provider.name, it.action.name) }.orEmpty() },
    restore = { saved ->
        if (saved.isEmpty()) null
        else PendingLink(
            provider = SocialProvider.valueOf(saved[0]),
            action = ProviderLinkAction.valueOf(saved[1])
        )
    }
)

@Composable
private fun AccountCard(
    connectedProviders: Set<SocialProvider>,
    onSignOutClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "JD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = SIGNED_IN_NAME,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SIGNED_IN_EMAIL,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Driven off the same set as the sign-in methods card so the two can't drift.
                    val linkedProviders = SocialProvider.entries.filter { it in connectedProviders }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        linkedProviders.forEach { provider ->
                            Icon(
                                painter = painterResource(provider.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (provider.tintWithTheme) MaterialTheme.colorScheme.primary
                                else Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = linkedProviders.singleOrNull()
                                ?.let { "SIGNED IN WITH ${it.displayName.uppercase()}" }
                                ?: "${linkedProviders.size} SIGN-IN METHODS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable(onClick = onSignOutClick)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "Sign Out",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SignedOutCard(onSignInClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "You're signed out",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sign in to back up your memories and keep them in step across devices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = onSignInClick
            ) {
                Text(
                    text = "Sign in or create account",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SyncStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            SyncSettingRow(
                title = "Sync across devices",
                subtitle = "Keep your memories updated everywhere",
                trailing = {
                    Switch(checked = true, onCheckedChange = {})
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Synced just now",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ManageBackupsCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_database_backup),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Manage backups",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_right),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FirebaseScreenPreview() {
    MemoriesTheme {
        FirebaseScreen()
    }
}
