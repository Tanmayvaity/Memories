package com.example.memories.feature.feature_firebase.presentation

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.presentation.UiState
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.feature.feature_firebase.domain.model.FirebaseUserData
import com.example.memories.feature.feature_firebase.domain.model.ProviderLinkAction
import com.example.memories.feature.feature_firebase.domain.model.SocialProvider
import com.example.memories.feature.feature_firebase.presentation.components.AccountCard
import com.example.memories.feature.feature_firebase.presentation.components.AuthSheet
import com.example.memories.feature.feature_firebase.presentation.components.LinkProviderSheet
import com.example.memories.feature.feature_firebase.presentation.components.LinkedAccountsCard
import com.example.memories.feature.feature_firebase.presentation.components.LogoutSheet
import com.example.memories.feature.feature_firebase.presentation.components.ManageBackupsCard
import com.example.memories.feature.feature_firebase.presentation.components.SignedOutCard
import com.example.memories.feature.feature_firebase.presentation.components.SyncSettingRow
import com.example.memories.feature.feature_firebase.presentation.components.SyncStatusCard
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun FirebaseRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: FirebaseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isCredentialsError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.oneTimeUiEvents.collect { event ->
            when (event) {
                is FirebaseUiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                FirebaseUiEvent.InvalidCredentials -> isCredentialsError = true
            }
        }
    }

    FirebaseScreen(
        modifier = modifier,
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        isCredentialsError = isCredentialsError,
        onCredentialsErrorCleared = { isCredentialsError = false },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseScreen(
    modifier: Modifier = Modifier,
    state: FirebaseState = FirebaseState(),
    onEvent: (FirebaseEvents) -> Unit = {},
    onBack: () -> Unit = {},
    isCredentialsError: Boolean = false,
    onCredentialsErrorCleared: () -> Unit = {},
) {
    var showLogoutSheet by rememberSaveable { mutableStateOf(false) }
    var showAuthSheet by rememberSaveable { mutableStateOf(false) }
    var connectedProviders by rememberSaveable(stateSaver = ConnectedProvidersSaver) {
        mutableStateOf(setOf(SocialProvider.GOOGLE))
    }
    var pendingLink by rememberSaveable(stateSaver = PendingLinkSaver) {
        mutableStateOf<PendingLink?>(null)
    }

    val signedInUser = (state.userState as? UiState.Success)?.data

    LaunchedEffect(state.isSignedIn) {
        if (state.isSignedIn) {
            showAuthSheet = false
            showLogoutSheet = false
        }
    }

    if (showLogoutSheet) {
        LogoutSheet(
            email = signedInUser?.email.orEmpty(),
            onConfirmLogout = {
                onEvent(FirebaseEvents.LogoutEvent)
                showLogoutSheet = false
            },
            onDismiss = { showLogoutSheet = false }
        )
    }

    if (showAuthSheet) {
        AuthSheet(
            authMode = state.authMode,
            onAuthModeChange = { mode ->
                onCredentialsErrorCleared()
                onEvent(FirebaseEvents.AuthModeChanged(mode))
            },
            isLoading = state.isAuthLoading,
            isCredentialsError = isCredentialsError,
            onCredentialsErrorCleared = onCredentialsErrorCleared,
            onSubmit = { mode, email, password ->
                when (mode) {
                    AuthMode.LOGIN -> onEvent(FirebaseEvents.SignInEvent(email, password))
                    AuthMode.REGISTER -> onEvent(FirebaseEvents.CreateUserEvent(email, password))
                }
            },
            onSocialSignIn = { showAuthSheet = false },
            onForgotPassword = { /* TODO */ },
            onDismiss = {
                onCredentialsErrorCleared()
                showAuthSheet = false
            }
        )
    }

    pendingLink?.let { pending ->
        LinkProviderSheet(
            provider = pending.provider,
            action = pending.action,
            email = signedInUser?.email.orEmpty(),
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
        },
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

            val accountCardState = when {
                state.isSignedIn -> AccountCardState.SignedIn
                state.isAuthLoading -> AccountCardState.Authenticating
                else -> AccountCardState.SignedOut
            }

            AnimatedContent(targetState = accountCardState, label = "accountCard") { cardState ->
                when (cardState) {
                    AccountCardState.SignedIn -> {
                        AccountCard(
                            user = signedInUser,
                            connectedProviders = connectedProviders,
                            onSignOutClick = { showLogoutSheet = true },
                        )
                    }

                    AccountCardState.Authenticating -> {
                        AccountCard(
                            user = null,
                            connectedProviders = emptySet(),
                            onSignOutClick = {},
                            isLoading = true,
                            loadingMessage = when (state.authMode) {
                                AuthMode.LOGIN -> "Signing in…"
                                AuthMode.REGISTER -> "Creating account…"
                            },
                        )
                    }

                    AccountCardState.SignedOut -> {
                        SignedOutCard(onSignInClick = { showAuthSheet = true })
                    }
                }
            }

            AnimatedVisibility(visible = state.isSignedIn) {
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
                            "Signing in with any connected provider takes you to ${signedInUser?.email.orEmpty()}."
                        } else {
                            "Connect another provider to sign in to ${signedInUser?.email.orEmpty()} any way you " +
                                    "like. You'll always need at least one."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

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

            ManageBackupsCard(onClick = { /* TODO */ })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private enum class AccountCardState {
    SignedOut,
    Authenticating,
    SignedIn,
}

private data class PendingLink(
    val provider: SocialProvider,
    val action: ProviderLinkAction,
)

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

@PreviewLightDark
@Preview(showBackground = true, name = "Signed in")
@Composable
private fun FirebaseScreenPreview() {
    MemoriesTheme {
        FirebaseScreen(
            state = FirebaseState(
                authMode = AuthMode.LOGIN,
                userState = UiState.Success(
                    FirebaseUserData(
                        uid = "preview-user-123",
                        email = "tanmay@example.com",
                        displayName = "Tanmay",
                    )
                ),
            ),
        )
    }
}

@Preview(showBackground = true, name = "Authenticating")
@Composable
private fun FirebaseScreenAuthenticatingPreview() {
    MemoriesTheme {
        FirebaseScreen(
            state = FirebaseState(
                authMode = AuthMode.LOGIN,
                userState = UiState.Loading,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Signed out")
@Composable
private fun FirebaseScreenSignedOutPreview() {
    MemoriesTheme {
        FirebaseScreen(state = FirebaseState())
    }
}
