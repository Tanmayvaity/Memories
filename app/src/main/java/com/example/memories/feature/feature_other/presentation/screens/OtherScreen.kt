package com.example.memories.feature.feature_other.presentation.screens

import android.app.ProgressDialog.show
import android.content.Intent
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.LocalTheme
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import com.example.memories.feature.feature_other.presentation.screens.components.ThemeBottomSheet
import com.example.memories.navigation.AppScreen
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

const val TAG = "OtherScreen"

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun OtherScreen(
    onNavigateToTags: (AppScreen.Tags) -> Unit = {},
    onNavigateToSettingsScreen : (AppScreen.NotificationSettings) -> Unit = {}
) {

    val context = LocalContext.current
    val viewmodel = hiltViewModel<ThemeViewModel>()
    val state by viewmodel.isDarkModeEnabled.collectAsStateWithLifecycle()
    val theme = LocalTheme.current
    val scrollState = rememberScrollState()
    var showThemeSheet by remember { mutableStateOf(false) }




    val generalSettingItems = listOf<MenuItem>(
        MenuItem(
            icon = R.drawable.ic_notification,
            iconContentDescription = "Notifications Icon",
            title = "Notifications",
            content = "Manage Your Notifications",
            onClick = {
                onNavigateToSettingsScreen(AppScreen.NotificationSettings)
            }
        ),
        MenuItem(
            icon = R.drawable.ic_storage,
            iconContentDescription = "Storage Icon",
            title = "Storage",
            content = "View app's storage information",
            onClick = {}
        ),
        MenuItem(
            icon = R.drawable.ic_database_backup,
            iconContentDescription = "Database backup icon",
            title = "Database backup",
            content = "Take database backup",
            onClick = {}
        ),
        MenuItem(
            icon = R.drawable.ic_theme,
            iconContentDescription = "Theme icon",
            title = "Change Theme",
            content = "Toggle between light and dark theme",
            onClick = {
                showThemeSheet = true
            }
        ),
        MenuItem(
            icon = R.drawable.ic_tag,
            iconContentDescription = "Tag Icon",
            title = "Tags Info",
            content = "Check and edit your created tags",
            onClick = {
                onNavigateToTags(AppScreen.Tags)
            }
        )

    )
    val appInfoSettingItems = listOf<MenuItem>(
        MenuItem(
            icon = R.drawable.ic_app_version,
            iconContentDescription = "App Version Icon",
            title = "App Version",
            content = "1.0.0",
            onClick = {
                val developerUri = "https://github.com/Tanmayvaity/Memories"
                val intent = Intent(Intent.ACTION_VIEW, developerUri.toUri())
                context.startActivity(intent)
            }
        ),
        MenuItem(
            icon = R.drawable.ic_terms,
            iconContentDescription = "Terms of service icon",
            title = "Terms of service",
            content = null
        ),
        MenuItem(
            icon = R.drawable.ic_privacy,
            iconContentDescription = "Privacy Policy Icon",
            title = "Privacy policy",
            content = null
        ),
        MenuItem(
            icon = R.drawable.ic_developer,
            iconContentDescription = "Developer Info Icon",
            title = "Developer Info",
            content = "View developer info",
            onClick = {
                val developerUri = "https://github.com/Tanmayvaity"
                val intent = Intent(Intent.ACTION_VIEW, developerUri.toUri())
                context.startActivity(intent)
            }
        )

    )



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
                .padding(10.dp)
                .verticalScroll(state = scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {

            Text(
                text = "GENERAL",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp),

                )

            generalSettingItems.forEach { item ->
                CustomSettingRow(
                    drawableRes = item.icon,
                    contentDescription = item.iconContentDescription ?: "",
                    heading = item.title,
                    content = item.content ?: "",
                    onClick = item.onClick
                )
            }


            Text(
                text = "APP INFO",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)
            )

            appInfoSettingItems.forEach { item ->
                CustomSettingRow(
                    drawableRes = item.icon,
                    contentDescription = item.iconContentDescription ?: "",
                    heading = item.title,
                    content = item.content ?: "",
                    onClick = item.onClick
                )
            }
        }

        if (showThemeSheet) {
            ThemeBottomSheet(
                btnText = "Apply Theme",
                heading = "Change Theme",
                subHeading = "Choose your prefered theme",
                isDarkMode = state,
                onApplyTheme = {
                    showThemeSheet = false
                    viewmodel.onEvent(
                        ThemeEvents.SetTheme
                    )
                },
                themeOptions = listOf(
                    MenuItem(
                        title = "Light Mode",
                        icon = R.drawable.ic_light_mode,
                        iconContentDescription = "Light Mode Icon",
                        onClick = {
                            viewmodel.onEvent(
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
                            viewmodel.onEvent(
                                ThemeEvents.ChangeThemeType(
                                    ThemeTypes.DARK

                                )
                            )
                        }
                    ),
//                    MenuItem(
//                        title = "System Default",
//                        icon = R.drawable.ic_theme,
//                        iconContentDescription = "System Default Icon",
//                        onClick = {
//                            viewmodel.onEvent(ThemeEvents.ChangeThemeType(
//                                ThemeTypes.SYSTEM
//                            ))
//                        }
//                    )
                ),
                onDismiss = {
                    showThemeSheet = false
                }
            )

        }
    }
}

@Composable
fun CustomSettingRow(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int,
    contentDescription: String,
    heading: String,
    content: String = "",
    color: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {},
    endContent: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_right),
            contentDescription = "Open $heading Memories",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
//                .weight(1f)
                .padding(end = 10.dp)


        )
    }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconItem(
            drawableRes = drawableRes,
            modifier = Modifier
                .padding(10.dp),
//                .weight(1f)
            contentDescription = contentDescription,
            color = iconColor,
            backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            alpha = 0.1f,
        )
        Column(
            verticalArrangement = Arrangement.Center,

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp)
        ) {
            Text(
                text = heading,
                modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                fontSize = 16.sp,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
            if (content.isNotEmpty() || content.isNotEmpty()) {
                Text(
                    text = content,
                    modifier = Modifier.padding(start = 5.dp, top = 0.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
        endContent()


    }


}