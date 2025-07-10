package com.example.memories.feature.feature_camera.presentation.camera.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.IconItem

@Composable
fun UpperBox(
    modifier: Modifier = Modifier,
    torchState: Boolean,
    onTorchToggle: () -> Unit = {},
    onTimerSet: () -> Unit = {},
    onAspectRatioChange: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {}
//            .background(Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
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
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = R.drawable.ic_timer,
                contentDescription = "Photo capture timer",
                alpha = 0.1f,
                onClick = {
                    onTimerSet()
                }
            )
            // night mode
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = R.drawable.ic_night_mode,
                contentDescription = "Toggle night mode on/off",
                alpha = 0.1f
            )
            // aspect ratio
            IconItem(
                modifier = Modifier.padding(5.dp),
                drawableRes = R.drawable.ic_aspect,
                contentDescription = "Change Aspect Ratio",
                alpha = 0.1f
            ) {
                onAspectRatioChange()
            }
        }
    }
}
