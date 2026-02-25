package com.example.memories.feature.feature_other.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun PinPad(
    modifier: Modifier = Modifier,
    onDigitClick: (Int) -> Unit = {},
    onBackSpaceClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)

    ) {
        for (row in 0 until 3) {
            PinRow(row = row, onDigitClick = {digit -> onDigitClick(digit)})
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (0..2).forEachIndexed { index, i ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (index == 0) {
                        Box(modifier.size(64.dp))
                    }
                    if (index == 1) {
                        PinKey(
                            digit = 0,
                            onClick = {digit -> onDigitClick(digit)},
                        )
                    }
                    if (index == 2) {
                        PinKey(
                            digit = 0,
                            onClick = {
                                onBackSpaceClick()
                            },
                            iconRes = R.drawable.ic_backspace,
                            contentDescription = "backspace icon"
                        )
                    }
                }

            }


        }


    }
}

@Composable
fun PinRow(
    modifier: Modifier = Modifier,
    row: Int,
    onDigitClick: (Int) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (col in 1..3) {
            val value = row * 3 + col
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ){
                PinKey(
                    digit = value,
                    onClick = { digit ->
                        onDigitClick(digit)
                    },
                )
            }

        }
    }
}

@Preview
@Composable
private fun PinPadPreview() {
    MemoriesTheme {
        PinPad()
    }
}


@Preview
@Composable
private fun PinRowPreview() {
    MemoriesTheme {
        PinRow(
            row = 9
        )
    }
}