package com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.presentation.BiometricPromptManager
import com.example.memories.feature.feature_other.presentation.BiometricResult
import com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components.BiometricResultBottomSheet
import com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components.BiometricSheetAction
import com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components.PinSuccessBottomSheet
import com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components.SetupPinDialog
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingEvents
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingScreenState
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingViewModel
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenScreenOneTimeEvent
import com.example.memories.ui.theme.MemoriesTheme
import javax.inject.Inject


@Composable
fun HiddenMemorySettingRoot(
    onBack: () -> Unit,
    viewModel: HiddenMemorySettingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HiddenScreenOneTimeEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    HiddenMemorySettingScreen(
        onBack = onBack,
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenMemorySettingScreen(
    onBack: () -> Unit = {},
    state: HiddenMemorySettingScreenState = HiddenMemorySettingScreenState(),
    onEvent: (HiddenMemorySettingEvents) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showPinSetupDialog by rememberSaveable { mutableStateOf(false) }
    var showPinSetupConfirmSheet by rememberSaveable { mutableStateOf(false) }
    var showBiometricSheet by rememberSaveable { mutableStateOf(false) }
    var currentBiometricResult by rememberSaveable { mutableStateOf<BiometricResult?>(null) }
    var currentLockMethod by rememberSaveable { mutableStateOf<LockMethod?>(null) }
    val activity = context as AppCompatActivity
    val biometricManager = remember {
        BiometricPromptManager(activity)
    }

    LaunchedEffect(Unit) {
        biometricManager.biometricResults.collect { result ->
            currentBiometricResult = result
            showBiometricSheet = true
            when (currentBiometricResult) {
                BiometricResult.AuthenticationSuccess -> {
                    if (currentLockMethod != null) {
                        onEvent(
                            HiddenMemorySettingEvents.SetLockMethod(
                                currentLockMethod!!
                            )
                        )
                    }
                }

                else -> {
                    onEvent(HiddenMemorySettingEvents.SetLockMethod(LockMethod.NONE))
                    currentLockMethod = LockMethod.NONE
                }
            }

        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                title = {
                    Text(
                        text = "Hidden Memories Settings",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            HeaderSection()

            LockMethodSection(
                currentLockMethod = state.currentLockMethod,
                onSetLockMethod = { lockMethod ->
                    if (lockMethod == LockMethod.CUSTOM_PIN) {
                        showPinSetupDialog = true
                        return@LockMethodSection
                    }
                    if (lockMethod == LockMethod.NONE) {
                        onEvent(HiddenMemorySettingEvents.SetLockMethod(lockMethod))
                    }
                    currentLockMethod = lockMethod
                    when (lockMethod) {
                        LockMethod.DEVICE_PATTERN -> {

                            biometricManager.showBiometricPrompt(
                                title = "Unlock Hidden Memories",
                                description = "Verify your device PIN, pattern, or password to access your hidden memories.",
                                lockMethod = lockMethod
                            )
                        }

                        LockMethod.DEVICE_BIOMETRIC -> {
                            biometricManager.showBiometricPrompt(
                                title = "Unlock Hidden Memories",
                                description = "Use your fingerprint or face to access your hidden memories.",
                                lockMethod = lockMethod
                            )

                        }

                        else -> {}
                    }

                },
            )

            Spacer(modifier = Modifier.height(32.dp))

            LockDurationSection(
                currentLockDuration = state.currentLockDuration,
                onSetLockDuration = { onEvent(HiddenMemorySettingEvents.SetLockDuration(it)) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DataManagementSection(
                onUnhideAll = {
                    onEvent(HiddenMemorySettingEvents.UnHideAllHiddenMemories)
                },
                onDeleteAll = {
                    onEvent(HiddenMemorySettingEvents.DeleteAllHiddenMemories)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showPinSetupDialog) {
            SetupPinDialog(
                onDismiss = { success ->
                    if (!success && !state.isCustomPinSet) {
                        onEvent(HiddenMemorySettingEvents.SetLockMethod(LockMethod.NONE))
                        currentLockMethod = LockMethod.NONE
                    }
                    if (success) {
                        showPinSetupConfirmSheet = true
                        onEvent(HiddenMemorySettingEvents.SetLockMethod(LockMethod.CUSTOM_PIN))
                        currentLockMethod = LockMethod.CUSTOM_PIN
                    }
                    showPinSetupDialog = false
                },
                onPinChange = { pin ->
                    onEvent(HiddenMemorySettingEvents.SetCustomPin(pin))
                }
            )
        }
        if (showPinSetupConfirmSheet) {
            PinSuccessBottomSheet(
                onDismiss = {
                    showPinSetupConfirmSheet = false
                }
            )
        }

        if (showBiometricSheet && currentBiometricResult != null) {
            BiometricResultBottomSheet(
                result = currentBiometricResult!!
            ) { sheetAction ->
                when (sheetAction) {
                    BiometricSheetAction.OPEN_SETTINGS -> {
                        if (Build.VERSION.SDK_INT >= 30) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )

                            }
                            context.startActivity(enrollIntent)
                        }
                    }

                    BiometricSheetAction.USE_CUSTOM_PIN -> {
                        showPinSetupDialog = true
                    }

                    BiometricSheetAction.RETRY -> {
                        when (currentLockMethod) {
                            LockMethod.DEVICE_PATTERN -> {

                                biometricManager.showBiometricPrompt(
                                    title = "Unlock Hidden Memories",
                                    description = "Verify your device PIN, pattern, or password to access your hidden memories.",
                                    lockMethod = currentLockMethod!!
                                )
                            }

                            LockMethod.DEVICE_BIOMETRIC -> {
                                biometricManager.showBiometricPrompt(
                                    title = "Unlock Hidden Memories",
                                    description = "Use your fingerprint or face to access your hidden memories.",
                                    lockMethod = currentLockMethod!!
                                )

                            }

                            else -> {}
                        }
                    }

                    BiometricSheetAction.DISMISS -> {
                        showBiometricSheet = false
//                        when (currentBiometricResult) {
//                            BiometricResult.AuthenticationSuccess -> {
//                                if (currentLockMethod != null) {
//                                    onEvent(
//                                        HiddenMemorySettingEvents.SetLockMethod(
//                                            currentLockMethod!!
//                                        )
//                                    )
//                                }
//
//                                showBiometricSheet = false
//                            }
//
//                            else -> {
//                                onEvent(HiddenMemorySettingEvents.SetLockMethod(LockMethod.NONE))
//                                currentLockMethod = LockMethod.NONE
//                                showBiometricSheet = false
//                            }
//                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconItem(
                drawableRes = R.drawable.ic_hidden,
                contentDescription = "Hidden Icon",
                modifier = Modifier
                    .size(128.dp),
                iconSize = 64.dp,
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                alpha = 0.1f
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage how your hidden memories are secured and displayed within the app",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LockMethodSection(
    currentLockMethod: LockMethod,
    onSetLockMethod: (LockMethod) -> Unit,
) {
    SectionTitle("Lock Method")
    Spacer(modifier = Modifier.height(16.dp))
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            LockMethod.entries.forEachIndexed { index, method ->
                CustomSettingRow(
                    modifier = Modifier.padding(8.dp),
                    heading = method.title,
                    content = method.description,
                    drawableRes = method.icon,
                    contentDescription = method.description,
                    showDivider = index != LockMethod.entries.lastIndex,
                    showCustomContent = index == LockMethod.CUSTOM_PIN.ordinal,
                    customContent = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    onSetLockMethod(method)
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_lock),
                                        contentDescription = "Lock icon"
                                    )
                                    Text(text = "Setup/Change PIN")
                                }
                            }
                        }
                    },
                    onClick = {
                        onSetLockMethod(method)
                    },
                    endContent = {
                        RadioButton(
                            selected = method == currentLockMethod,
                            onClick = { onSetLockMethod(method) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LockDurationSection(
    currentLockDuration: LockDuration,
    onSetLockDuration: (LockDuration) -> Unit
) {
    SectionTitle("Lock Duration")
    Spacer(modifier = Modifier.height(16.dp))
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            LockDuration.entries.forEachIndexed { index, duration ->
                CustomSettingRow(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onSetLockDuration(duration) },
                    heading = duration.title,
                    showDivider = index != LockDuration.entries.lastIndex,
                    showContentAtEnd = false,
                    showCustomContent = false,
                    endContent = {
                        RadioButton(
                            selected = duration == currentLockDuration,
                            onClick = { onSetLockDuration(duration) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DataManagementSection(
    onUnhideAll: () -> Unit,
    onDeleteAll: () -> Unit
) {
    SectionTitle("Data Management")
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = onUnhideAll,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Unhide All Hidden Memories")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDeleteAll,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon"
                )
                Text(text = "Delete All Hidden Memories")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Note: Deleting Hidden Memories is permanent",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
private fun HiddenMemorySettingScreenPreview() {
    MemoriesTheme {
        HiddenMemorySettingScreen()
    }
}