package com.example.memories.core.presentation.components

import android.R.attr.enabled
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.iconSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun LoadingIconItem(
    modifier: Modifier = Modifier,
    isLoading : Boolean = false,
    @DrawableRes drawableRes : Int,
    contentDescription : String? = null,
    onClick : () -> Unit = {},
    enabled : Boolean = true,
    shape : Shape = CircleShape,
    backgroundColor : Color = MaterialTheme.colorScheme.onSurfaceVariant,
    alpha : Float = 0.1f,
    color : Color = MaterialTheme.colorScheme.onSurface,
    iconSize : Dp = 24.dp
) {
    Surface(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        IconButton(
            enabled = enabled,
            onClick = {
                onClick()
            },
            modifier = modifier
                .clip(shape)
                .background(backgroundColor.copy(alpha = alpha))
        ) {
            if(!isLoading){
                Icon(
                    painter = painterResource(drawableRes),
                    contentDescription = contentDescription,
                    tint = color,
                    modifier = Modifier
                        .size(iconSize),
                )
            }else{
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 1.dp,
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f),
                    gapSize = 0.dp
                )
            }

        }
    }

}


@Preview
@Composable
private fun LoadingIconItemPreview() {
    MemoriesTheme {
        LoadingIconItem(
            drawableRes = R.drawable.ic_download,
            isLoading = true
        )
    }
}