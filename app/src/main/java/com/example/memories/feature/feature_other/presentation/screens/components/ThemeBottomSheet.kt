package com.example.memories.feature.feature_other.presentation.screens.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.core.presentation.MenuItem
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.R
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.feature.feature_other.presentation.ThemeTypes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBottomSheet(
    modifier: Modifier = Modifier,
    heading : String,
    subHeading : String,
    themeOptions : List<MenuItem>,
    onApplyTheme : () -> Unit = {},
    onDismiss : () -> Unit = {},
    state : SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    isDarkMode : ThemeTypes = ThemeTypes.DARK,
    btnText : String = ""
) {
    var isSelected  by remember { mutableStateOf(false) }
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeadingText(
                title = heading.toString(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
            )
            themeOptions.forEachIndexed { index,item ->
                Card(
                    border = BorderStroke(
                        width = 1.dp,
                        color = if(index == isDarkMode.toIndex()) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if(index == isDarkMode.toIndex())MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                ) {
                    CustomSettingRow(
                        drawableRes = item.icon,
                        contentDescription = item.iconContentDescription?: "",
                        heading = item.title,
                        content = item.content ?: "",
                        onClick = {
                            item.onClick()
                        },
                        endContent = {
                            RadioButton(
                                selected = index == isDarkMode.toIndex(),
                                onClick = {
                                    item.onClick()
                                }
                            )
                        },
                        modifier = Modifier
                            .background(containerColor)
                            .border(width = 1.dp,color = borderColor)
                            .padding(10.dp)

                    )
                }
            }
            Button(
                onClick = onApplyTheme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                ,
            ) {
                Text(
                    text = btnText,
                    modifier = Modifier.padding(10.dp)
                )

            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 14.sp
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun ThemeBottomSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ThemeBottomSheet(
            btnText = "Apply Theme",
            heading = "Change Theme",
            subHeading = "Choose your prefered theme",
            themeOptions = listOf(
                MenuItem(
                    title = "Light Mode",
                    icon = R.drawable.ic_light_mode,
                    iconContentDescription = "Light Mode Icon",
                    onClick = {}
                ),
                MenuItem(
                    title = "Dark Mode",
                    icon = R.drawable.ic_night_mode,
                    iconContentDescription = "Dark Mode Icon",
                    onClick = {}
                ),
                MenuItem(
                    title = "System Default Mode",
                    icon = R.drawable.ic_theme,
                    iconContentDescription = "Light Mode Icon",
                    onClick = {}
                )

            )
        )
    }
}