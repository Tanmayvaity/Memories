package com.example.memories.feature.feature_other.presentation.screens

import android.R.attr.contentDescription
import android.R.attr.onClick
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.iconColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.util.getVersionName
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_other.presentation.AppInfoSettingType
import com.example.memories.feature.feature_other.presentation.GeneralSettingType
import com.example.memories.feature.feature_other.presentation.PrivacySettingType
import com.example.memories.feature.feature_other.presentation.SettingClickEvent
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import com.example.memories.feature.feature_other.presentation.screens.components.ThemeBottomSheet
import com.example.memories.feature.feature_other.presentation.viewmodels.OtherViewModel
import com.example.memories.navigation.AppScreen


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
    themeViewModel: ThemeViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel(),
    otherViewModel: OtherViewModel = viewModel()
) {
    val state by themeViewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    var showThemeBottomSheet by remember { mutableStateOf(false) }

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
            }

        }
    }

    OtherScreen(
        onSettingEvent = otherViewModel::settingClickEvent
    )


    if (showThemeBottomSheet) {
        ThemeBottomSheet(
            btnText = "Apply Theme",
            heading = "Change Theme",
            subHeading = "Choose your prefered theme",
            isDarkMode = state,
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
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(
    onSettingEvent: (SettingClickEvent) -> Unit = {}
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


