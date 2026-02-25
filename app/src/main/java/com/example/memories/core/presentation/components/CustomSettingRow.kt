package com.example.memories.core.presentation.components

import android.R.attr.contentDescription
import android.R.attr.onClick
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.iconColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.util.noRippleClickable
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun CustomSettingRow(
    modifier: Modifier = Modifier,
    @DrawableRes drawableRes: Int? = null,
    contentDescription: String? = null,
    heading: String,
    content: String = "",
    color: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    iconBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    showDivider : Boolean = false,
    onClick: () -> Unit = {},
    showContentAtEnd : Boolean = true,
    showCustomContent : Boolean = false,
    customContent : @Composable () -> Unit = {},
    endContent: (@Composable () -> Unit)? = null
) {


    val actualEndContent = remember(endContent) {
        endContent ?: @Composable {
            Icon(
                painter = painterResource(R.drawable.ic_right),
                contentDescription = "Open $heading",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }


    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(!showContentAtEnd){
               actualEndContent()
            }

            drawableRes?.let {
                IconItem(
                    drawableRes = drawableRes,
                    modifier = Modifier
                        .padding(10.dp),
//                .weight(1f)
                    contentDescription = contentDescription ?: "",
                    color = iconColor,
                    backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = CircleShape,
                    alpha = 0.1f,
                )
            }


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
                if(showCustomContent){
                    customContent()
                }

            }
            if(showContentAtEnd){
                actualEndContent()
            }



        }
        if(showDivider){
            HorizontalDivider()
        }
    }
}

@PreviewLightDark
@Composable
private fun CustomSettingRowPreview() {
    MemoriesTheme {
        CustomSettingRow(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            heading = "Hello world",
            content = "Just Some Description to fill the content",
            drawableRes = R.drawable.ic_edit,
            contentDescription = "Edit Icon",
        )
    }

}