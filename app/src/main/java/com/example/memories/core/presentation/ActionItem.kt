package com.example.memories.core.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.R
@Composable
fun ActionItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    itemText : String = "Text",
    textColor : Color = MaterialTheme.colorScheme.onSurface,
    icon : Int = R.drawable.ic_hidden,
    iconColor : Color = MaterialTheme.colorScheme.onSurface,
    iconContentDescription : String = "Icon Description",
    backgroundColor : Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f)
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { onClick() }
            )
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .size(36.dp)
                    .padding(5.dp),
                tint = iconColor
            )
            Text(
                text = itemText,
                modifier = Modifier.padding(15.dp),
                color = textColor
            )
        }
    }
}



@Preview
@Composable
fun ActionItemPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ActionItem()
    }

}
