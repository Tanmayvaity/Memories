package com.example.memories.core.presentation.components

import android.R.attr.dialogTitle
import android.R.attr.text
import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.memories.R
import com.example.memories.feature.feature_feed.presentation.feed.FeedEvents
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun GeneralAlertDialog(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    title: String = "",
    text: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    confirmButton: @Composable () -> Unit = {},
    dismissButton: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    AlertDialog(
        icon = {
            icon()
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            confirmButton()
        },
        dismissButton = {
            dismissButton()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAlertSheet(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {},
    title: String = "",
    content: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    state: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    ),
    isLoading: Boolean = false
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.delete_icon_anim))
    val progress by animateLottieCompositionAsState(composition)
        ModalBottomSheet(
            sheetState = state,
            onDismissRequest = onDismiss,
            containerColor = containerColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),

                ) {
//                IconItem(
//                    imageVector = Icons.Filled.Delete,
//                    contentDescription = "Delete Icon",
//                    color = Color.Red,
//                    backgroundColor = Color.Red,
//                    alpha = 0.2f,
//                )
                LottieAnimation(
                    composition = composition,
                    speed = 3f,
                    modifier = Modifier.size(100.dp),
                    iterations = LottieConstants.IterateForever
                )

                HeadingText(
                    title = title.toString(),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.dismiss),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (!isLoading) {
                            Text(
                                text = "Delete",
                                color = Color.White
                            )
                        }

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }

                    }
                }


            }
        }

}

@PreviewLightDark
@Preview
@Composable
fun GeneralAlertDialogPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        GeneralAlertDialog(
            title = "Delete Memory Alert",
            text = "Are you sure you want to delete this memory",
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    onClick = {
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {

                    }

                ) {
                    Text(
                        text = stringResource(R.string.dismiss),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GeneralAlertBottomSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        GeneralAlertSheet(
            title = "Delete Memory Alert",
            content = "Are you sure you want to delete this memory",
            state = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            isLoading = true
        )
    }
}
