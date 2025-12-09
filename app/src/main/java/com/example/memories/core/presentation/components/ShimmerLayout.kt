package com.example.memories.core.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = "shimmer_animation"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
fun ShimmerLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(shape)
                        .shimmerEffect()
                )


                Spacer(modifier = Modifier.padding(8.dp))

                Column {
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.7f)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth(0.5f)
                            .shimmerEffect()
                    )
                }

            }
        }
    } else {
        content()
    }
}


@Composable
fun ShimmerLayoutForImage(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    shape: Shape = RoundedCornerShape(28.dp),
    content: @Composable () -> Unit
) {
    if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                ,
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(shape)
                        .shimmerEffect(),
                )
            }
    } else {
        content()
    }
}


@Preview(showBackground = true)
@Composable
private fun ShimmerLayoutPreview() {
    ShimmerLayoutForImage(
        isLoading = true
    ) { }
}