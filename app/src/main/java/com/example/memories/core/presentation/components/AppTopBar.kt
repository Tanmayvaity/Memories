package com.example.memories.core.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.memories.feature.feature_memory.presentation.MemoryEvents
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title : String,
    showAction : Boolean = false,
    onActionClick : () -> Unit = {},
    actionContent : @Composable () -> Unit = {},
    actionText : String = "",
    showNavigationIcon : Boolean = false,
    onNavigationIconClick : () -> Unit = {},
    showDivider : Boolean  = true
) {

    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title
                )
            },
            navigationIcon = {
                if(showNavigationIcon){
                    IconButton(
                        onClick = {
                            onNavigationIconClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to previous screen"
                        )
                    }
                }
            },
            actions = {
                if(showAction){
                    actionContent()
//                    TextButton(
//                        onClick = {
//                            onActionClick()
//                        }
//                    ) {
//                        Text(
//                            text = actionText,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
                }
            }

        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }


}

@Preview
@Composable
fun AppTopBarPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        AppTopBar(
            title = "Memory App",
            showAction =  true,
            showNavigationIcon = true,
            actionText = "Create"
        )
    }
}