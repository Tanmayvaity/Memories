package com.example.memories.feature.feature_camera.presentation.camera.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun UpperBox(
    modifier: Modifier = Modifier,
    torchState: Boolean,
    onTorchToggle: () -> Unit = {},
    onTimerSet: () -> Unit = {},
    onAspectRatioChange: () -> Unit = {},
    isVideoPlaying: Boolean = false,
    isPictureTimerRunning : Boolean = false
) {


    Box(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {}


//            .background(Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // flash
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = if (torchState) R.drawable.ic_flash_off else R.drawable.ic_flash_on,
                contentDescription = "toggle flash on and off",
                alpha = 0.1f,
                onClick = {
                    onTorchToggle()
                }
            )

            // timer picture

            AnimatedVisibility(
                visible = !isVideoPlaying,
                ) {
                Column{
                    AnimatedVisibility (visible = !isPictureTimerRunning){
                        IconItem(
                            modifier = Modifier.padding(5.dp),
                            drawableRes = R.drawable.ic_timer,
                            contentDescription = "Photo capture timer",
                            alpha = 0.1f,
                            onClick = {
                                onTimerSet()
                            },
                            onSelectedIconColorToggleColor = Color.Yellow
                        )
                    }

                    IconItem(
                        modifier = Modifier.padding(5.dp),
                        drawableRes = R.drawable.ic_night_mode,
                        contentDescription = "Toggle night mode on/off",
                        alpha = 0.1f
                    )

                    IconItem(
                        modifier = Modifier.padding(5.dp),
                        drawableRes = R.drawable.ic_aspect,
                        contentDescription = "Change Aspect Ratio",
                        alpha = 0.1f,
                        onClick = {
                            onAspectRatioChange()
                        })
                }

            }

            // night mode

            // aspect ratio

        }
    }
}
