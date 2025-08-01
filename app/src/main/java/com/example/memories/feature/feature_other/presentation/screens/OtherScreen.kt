package com.example.memories.feature.feature_other.presentation.screens

import android.R.attr.contentDescription
import android.R.attr.onClick
import android.content.Intent
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.navigation.AppScreen
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlinx.coroutines.delay

const val TAG = "OtherScreen"

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun OtherScreen(
    onCameraSettingsClick: (AppScreen.CameraSettings) -> Unit = {}
) {

    val context = LocalContext.current
    val viewmodel = hiltViewModel<ThemeViewModel>()
    val isDarkModeEnabled by viewmodel.isDarkModeEnabled.collectAsStateWithLifecycle()
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

            CustomSettingRow(
                drawableRes = R.drawable.ic_notification,
                contentDescription = "Notification Settings Icon",
                heading = "Notifications",
                content = "Manage Your Notifications"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_storage,
                contentDescription = "Storage Info Icon",
                heading = "Storage",
                content = "View your Storage Information"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_database_backup,
                contentDescription = "Database Backup Icon",
                heading = "Database backup",
                content = "Take database backup"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_language,
                contentDescription = "Language icon",
                heading = "Language",
                content = "Change Language"
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_theme,
                contentDescription = "Theme icon",
                heading = "Change Theme",
                content = "Toggle between light and dark theme",
                onSwitchStateChange = {
                    viewmodel.onEvent(ThemeEvents.SetTheme)
                    Log.d(TAG, "OtherScreen: ${isDarkModeEnabled} ")
                },
                checked = isDarkModeEnabled

            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_camera,
                contentDescription = "Camera Icon",
                heading = "Camera Settings",
                content = "View and Edit your camera settings",
            ) {
                onCameraSettingsClick(AppScreen.CameraSettings)
            }


            Text(
                text = "MEMORIES",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)

            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_archive,
                contentDescription = "Archived Memories Icon",
                heading = "Archive",
                content = "Manage your archived photos and vidoes"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_hidden,
                contentDescription = "Hidden memories icon",
                heading = "Hidden",
                content = "Manage your hidden photos and videos"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_memory,
                contentDescription = "Manage memory icon",
                heading = "Memories",
                content = "Manage your Memories"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_bin,
                contentDescription = "Bin icon",
                heading = "Bin",
                content = "View your items in the bin"
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_delete,
                contentDescription = "Delete Memories icon",
                heading = "Delete your data",
                content = "Wipe your entire data including memories,photos and videos",
                color = Color.Red
            )
            Text(
                text = "APP INFO",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp)
            )

            CustomSettingRow(
                drawableRes = R.drawable.ic_app_version,
                contentDescription = "App Version Icon",
                heading = "App Version",
                content = "1.0.0"
            ) {
                val developerUri = "https://github.com/Tanmayvaity/Memories"
                val intent = Intent(Intent.ACTION_VIEW, developerUri.toUri())
                context.startActivity(intent)
            }
            CustomSettingRow(
                drawableRes = R.drawable.ic_terms,
                contentDescription = "Terms of service icon",
                heading = "Terms of service",
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_privacy,
                contentDescription = "Privacy Policy Icon",
                heading = "Privacy policy",
            )
            CustomSettingRow(
                drawableRes = R.drawable.ic_developer,
                contentDescription = "Developer Info Icon",
                heading = "Developer Info",
                content = "View developer info",
                onClick = {
                    val developerUri = "https://github.com/Tanmayvaity"
                    val intent = Intent(Intent.ACTION_VIEW, developerUri.toUri())
                    context.startActivity(intent)

                },
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
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
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
                .padding(10.dp)
                .weight(1f),
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
                .weight(5f)
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

        Icon(
            painter = painterResource(R.drawable.ic_right),
            contentDescription = "Open $heading Memories",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
                .weight(1f)
                .size(24.dp)


        )


    }


}