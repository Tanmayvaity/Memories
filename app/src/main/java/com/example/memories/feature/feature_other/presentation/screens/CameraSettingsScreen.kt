package com.example.memories.feature.feature_other.presentation.screens

import android.R.attr.contentDescription
import android.R.attr.onClick
import androidx.annotation.DrawableRes
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.feature.feature_other.presentation.viewmodels.CameraSettingsEvents
import com.example.memories.feature.feature_other.presentation.viewmodels.CameraSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CameraSettingsScreen(
    modifier: Modifier = Modifier,
    onBack : () -> Unit = {},
) {
    val cameraSettingsViewModel : CameraSettingsViewModel = hiltViewModel<CameraSettingsViewModel>()
    val cameraSetingsState by cameraSettingsViewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar ={
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Camera Settings"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back to the previous screen"
                        )
                    }
                }

            )
        }

    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ){
            Text(
                text = "GENERAL",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp),
                )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_volume_on,
                contentDescription = "Shutter sound toggle",
                heading = "Shutter Sound",
                content = "Create a shutter sound when a photo is captured.",
                checked = cameraSetingsState.shutterSound,
                onSwitchStateChange = {it->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.ShutterSoundToggle(it))
                }
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_location,
                contentDescription = "Save location toggle",
                heading = "Save Location",
                content = "Add location to your pictures and videos",
                checked = cameraSetingsState.saveLocation,
                onSwitchStateChange = {it->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.SaveLocationToggle(it))
                }
            )

            Text(
                text = "COMPOSITION",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp),
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_camera_flip,
                contentDescription = "Mirror image toggle",
                heading = "Save selfies as previewed",
                content = "Save selfies as they appear on the screen",
                checked = cameraSetingsState.mirrorImage,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.MirrorImageToggle(it))
                }
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_grid,
                contentDescription = "Grid toggle",
                heading = "Show grid lines",
                content = "Show grid lines on the camera preview",
                checked = cameraSetingsState.gridLines,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.GridImageToggle(it))
                }
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_flip,
                contentDescription = "Toggle flip input",
                heading = "Toggle between front/back camera",
                content = "Toggle between front/back camera using upward flip.",
                checked = cameraSetingsState.flipCameraUsingSwipe,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.FlipCameraToggle(it))
                }
            )

            Text(
                text = "CAPTURE SETTINGS",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(10.dp),
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_watermark,
                contentDescription = "Set watermark",
                heading = "Watermark",
                content = "Set watermark while capturing photos and videos",
                checked = cameraSetingsState.watermark,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.WatermarkToggle(it))
                }
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_aperture,
                contentDescription = "Set high efficiency pictures",
                heading = "High efficiency pictures",
                content = "Save space by capturing pictures in HEIF image format.",
                checked = cameraSetingsState.heifPictures,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.HighEfficiencyPicturesToggle(it))
                }
            )
            CustomCameraSettingsRow(
                drawableRes = R.drawable.ic_video,
                contentDescription = "Set high efficiency Videos",
                heading = "High efficiency videos",
                content = "Save space by capturing videos in HEVC video format.",
                checked = cameraSetingsState.hevcVideos,
                onSwitchStateChange = {it ->
                    cameraSettingsViewModel.onEvent(CameraSettingsEvents.HighEfficiencyVideosToggle(it))
                }
            )





        }

    }

}


@Preview()
@Composable
fun CustomCameraSettingsRow(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int = R.drawable.ic_volume_on,
    contentDescription: String = "Default content description",
    heading: String = "Default Heading",
    content: String = "Default Content",
    color: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    onSwitchStateChange : (Boolean) -> Unit = {},
    checked : Boolean = false
) {


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
            .background(MaterialTheme.colorScheme.surface)
        ,
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
                .weight(4f)
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
        Switch(
            checked = checked,
            onCheckedChange = {
                onSwitchStateChange(it)
            },
            modifier = Modifier
                .weight(1f)
        )



    }


}