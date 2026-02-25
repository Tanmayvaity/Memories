package com.example.memories.feature.feature_other.presentation.screens.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun PinKey(
    modifier: Modifier = Modifier,
    digit: Int= 0,
    onClick: (Int) -> Unit,
    iconRes : Int? = null,
    contentDescription : String? = null
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 1f))
            .clickable {
                onClick(digit)
            },
        contentAlignment = Alignment.Center
    ) {
        if(iconRes != null && contentDescription != null){
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }else{
            Text(
                text = digit.toString(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        }

    }


}

@Preview
@Composable
private fun PinKeyPreview() {
    MemoriesTheme {
        PinKey(
            digit = 7,
            onClick = {},
            iconRes = R.drawable.ic_backspace,
            contentDescription = "backspace icon"
        )
    }
}