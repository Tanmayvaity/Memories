package com.example.memories.feature.feature_other.presentation.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.core.presentation.components.LoadingIconItem
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.presentation.AppInfoSettingType
import com.example.memories.feature.feature_other.presentation.BiometricPromptManager
import com.example.memories.feature.feature_other.presentation.BiometricResult
import com.example.memories.feature.feature_other.presentation.GeneralSettingType
import com.example.memories.feature.feature_other.presentation.PrivacySettingType
import com.example.memories.feature.feature_other.presentation.SettingClickEvent
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import com.example.memories.feature.feature_other.presentation.screens.components.LockMethodRequiredBottomSheet
import com.example.memories.feature.feature_other.presentation.screens.components.ThemeBottomSheet
import com.example.memories.feature.feature_other.presentation.screens.components.PinDismissReason
import com.example.memories.feature.feature_other.presentation.screens.components.PinType
import com.example.memories.feature.feature_other.presentation.screens.components.SetupPinDialog
import com.example.memories.feature.feature_other.presentation.viewmodels.OtherEvents
import com.example.memories.feature.feature_other.presentation.viewmodels.OtherState
import com.example.memories.feature.feature_other.presentation.viewmodels.OtherViewModel
import com.example.memories.navigation.AppScreen
import kotlinx.coroutines.delay


const val TAG = "OtherScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherRoot(
    onNavigateToTags: (AppScreen.Tags) -> Unit,
    onNavigateToSettingsScreen: (AppScreen.NotificationSettings) -> Unit,
    onNavigateToAboutScreen: (AppScreen.About) -> Unit,
    onNavigateToDeveloperInfoScreen: (AppScreen.DeveloperInfo) -> Unit,
    onNavigateToDeleteAllDataScreen: (AppScreen.DeleteAllData) -> Unit,
    onNavigateToHistoryScreen: (AppScreen.History) -> Unit,
    onNavigateToBackupScreen: (AppScreen.Backup) -> Unit,
    onNavigateToHiddenMemorySettingScreen: (AppScreen.HiddenMemorySetting) -> Unit,
    onNavigateToHiddenMemory: (AppScreen.HiddenMemory) -> Unit,
    themeViewModel: ThemeViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    otherViewModel: OtherViewModel = hiltViewModel()
) {
    val themeState by themeViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    val checkingPinValidity by otherViewModel.isLoading.collectAsStateWithLifecycle()
    val state by otherViewModel.state.collectAsStateWithLifecycle()
    var showThemeBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as AppCompatActivity
    var showCustomPinDialog by remember { mutableStateOf(false) }
    val biometricManager = remember(activity) {
        BiometricPromptManager(activity)
    }



    var customPinError by remember { mutableStateOf(false) }
    var showLockMethodRequiredSheet by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        biometricManager.biometricResults.collect { result ->
            if(result is BiometricResult.AuthenticationSuccess && state.lockMethod != null){
                onNavigateToHiddenMemory(AppScreen.HiddenMemory)
                return@collect
            }else if(result !is BiometricResult.AuthenticationError){
                showLockMethodRequiredSheet = true
            }
        }
    }

    LaunchedEffect(Unit) {
        otherViewModel.event.collect {  outcome ->
            if(outcome){
                onNavigateToHiddenMemory(AppScreen.HiddenMemory)
                delay(100)
                showCustomPinDialog = false
                customPinError = false
            }
            if(!outcome){
                customPinError = true
                delay(300)
                customPinError = false
            }
        }
    }

    LaunchedEffect(Unit) {
        otherViewModel.settingEvent.collect { event ->
            when (event) {
                SettingClickEvent.NOTIFICATION_ITEM_CLICK -> {
                    onNavigateToSettingsScreen(AppScreen.NotificationSettings)
                }

                SettingClickEvent.STORAGE_ITEM_CLICK -> {

                }

                SettingClickEvent.DATABASE_BACKUP_ITEM_CLICK -> {
                    onNavigateToBackupScreen(AppScreen.Backup)
                }

                SettingClickEvent.THEME_ITEM_CLICK -> {
                    showThemeBottomSheet = true
                }

                SettingClickEvent.TAG_ITEM_CLICK -> {
                    onNavigateToTags(AppScreen.Tags)
                }

                SettingClickEvent.HISTORY_ITEM_CLICK -> {
                    onNavigateToHistoryScreen(AppScreen.History)
                }

                SettingClickEvent.DELETE_ALL_DATA_ITEM_CLICK -> {
                    onNavigateToDeleteAllDataScreen(AppScreen.DeleteAllData)
                }

                SettingClickEvent.ABOUT_ITEM_CLICK -> {
                    onNavigateToAboutScreen(AppScreen.About)
                }

                SettingClickEvent.DEVELOPER_INFO_ITEM_CLICK -> {
                    onNavigateToDeveloperInfoScreen(AppScreen.DeveloperInfo)
                }

                SettingClickEvent.HIDDEN_ITEM_CLICK -> {
                    onNavigateToHiddenMemorySettingScreen(AppScreen.HiddenMemorySetting)
                }

                SettingClickEvent.HIDDEN_MEMORY_ITEM_CLICK -> {
                    when(state.lockMethod){
                        LockMethod.DEVICE_BIOMETRIC , LockMethod.DEVICE_PATTERN -> {
                            biometricManager.showBiometricPrompt(
                                title = "Unlock Hidden Memories",
                                description = "Use your fingerprint, face, PIN, or pattern to unlock hidden memories",
                                lockMethod = state.lockMethod!!
                            )
                        }
                        LockMethod.CUSTOM_PIN -> {
                            showCustomPinDialog = true
                        }
                        LockMethod.NONE -> {
                            onNavigateToHiddenMemory(AppScreen.HiddenMemory)
                        }
                        null -> {}
                    }

                }

            }

        }
    }

    OtherScreen(
        onSettingEvent = otherViewModel::settingClickEvent,
        state = state
    )


    if (showThemeBottomSheet) {
        ThemeBottomSheet(
            btnText = "Apply Theme",
            heading = "Change Theme",
            subHeading = "Choose your prefered theme",
            isDarkMode = themeState,
            onApplyTheme = {
                showThemeBottomSheet = false
                themeViewModel.onEvent(
                    ThemeEvents.SetTheme
                )
            },
            themeOptions = listOf(
                MenuItem(
                    title = "Light Mode",
                    icon = R.drawable.ic_light_mode,
                    iconContentDescription = "Light Mode Icon",
                    onClick = {
                        themeViewModel.onEvent(
                            ThemeEvents.ChangeThemeType(
                                ThemeTypes.LIGHT

                            )
                        )
                    }
                ),
                MenuItem(
                    title = "Dark Mode",
                    icon = R.drawable.ic_night_mode,
                    iconContentDescription = "Dark Mode Icon",
                    onClick = {
                        themeViewModel.onEvent(
                            ThemeEvents.ChangeThemeType(
                                ThemeTypes.DARK

                            )
                        )
                    }
                ),
            ),
            onDismiss = {
                showThemeBottomSheet = false
            }
        )

    }


    if(showCustomPinDialog){
        SetupPinDialog(
            onDismiss = { reason ->
                if( reason == PinDismissReason.CANCELLED){
                    showCustomPinDialog = false
                }

            },
            pinType = PinType.UNLOCK,
            onPinChange = { inputPin ->
                otherViewModel.onEvent(
                    OtherEvents.CheckPinValidity(inputPin)
                )
            },
            hasErrorOccured = customPinError,
            showChecking = checkingPinValidity

        )
    }
    if(showLockMethodRequiredSheet){
        LockMethodRequiredBottomSheet(
            onDismiss = {
                showLockMethodRequiredSheet = false
            },
            onGoToSettings = {
                showLockMethodRequiredSheet = false
                otherViewModel.settingClickEvent(SettingClickEvent.HIDDEN_ITEM_CLICK)
            }
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(
    onSettingEvent: (SettingClickEvent) -> Unit = {},
    state: OtherState = OtherState()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },

        ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(state = scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {

            Text(
                text = "GENERAL",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            GeneralSettingType.entries.forEach { item ->
                CustomSettingRow(
                    drawableRes = item.icon,
                    contentDescription = item.description ?: "",
                    heading = item.title,
                    content = item.description ?: "",
                    onClick = {
                        onSettingEvent(item.onClickEvent)
                    },
                    endContent = if (item == GeneralSettingType.HIDDEN_MEMORIES) {
                        {
                            LoadingIconItem(
                                isLoading = state.lockMethod == null,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f),
                                drawableRes = R.drawable.ic_right,
                                contentDescription = "Right Icon",
                                alpha = 0f
                            )
                        }
                    } else {
                        null
                    }
                )
            }

            Text(
                text = "PRIVACY AND SECURITY",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            PrivacySettingType.entries.forEach { item ->
                CustomSettingRow(
                    drawableRes = item.icon,
                    contentDescription = item.description ?: "",
                    heading = item.title,
                    content = item.description ?: "",
                    onClick = {
                        onSettingEvent(item.onClickEvent)
                    }
                )
            }


            Text(
                text = "APP INFO",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            AppInfoSettingType.entries.forEach { item ->
                CustomSettingRow(
                    drawableRes = item.icon,
                    contentDescription = item.description ?: "",
                    heading = item.title,
                    content = item.description ?: "",
                    onClick = {
                        onSettingEvent(item.onClickEvent)
                    }
                )
            }
        }


    }
}


