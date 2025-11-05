package com.example.memories.feature.feature_camera.presentation.camera.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.ui.theme.MemoriesTheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun UpperBox(
    modifier: Modifier = Modifier,
    torchState: Boolean,
    onTorchToggle: () -> Unit = {},
    onTimerSet: () -> Unit = {},
    onAspectRatioChange: () -> Unit = {},
    isVideoPlaying: Boolean = false,
    isPictureTimerRunning : Boolean = false,
    onToggleCamera : () -> Unit = {}
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
            IconItem(
                drawableRes = R.drawable.ic_camera_flip,
                contentDescription = "Toggle camera lens",
                color = Color.White,
                alpha = 0.1f,
                onClick = { onToggleCamera() },
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


@Preview
@Composable
fun UpperBoxPreview(){
    MemoriesTheme {
        UpperBox(
            torchState = false
        )
    }

}
