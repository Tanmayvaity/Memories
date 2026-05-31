package com.example.memories.feature.feature_feed.presentation.feed_detail.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.launch

/**
 * Previews a memory's share card and, on confirm, captures it to a bitmap (via a [GraphicsLayer])
 * and hands it back through [onShare].
 */
@Composable
fun ShareMemoryCardDialog(
    memory: MemoryWithMediaModel,
    isSharing: Boolean,
    isDownloading: Boolean,
    onShare: (Bitmap) -> Unit,
    onDownload: (Bitmap) -> Unit,
    onDismiss: () -> Unit,
    isShareMode : Boolean = false
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Share as image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Only this subtree is captured.
                Column(
                    modifier = Modifier.drawWithContent {
                        graphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(graphicsLayer)
                    }
                ) {
                    MemoryShareCard(memory = memory)
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, enabled = (!isDownloading || !isSharing)) {
                        Text("Cancel")
                    }
                    if(!isShareMode) {
                        Spacer(Modifier.width(8.dp))
                        Button(
                            enabled = !isDownloading && !isShareMode,
                            onClick = {
                                scope.launch {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    onDownload(bitmap)
                                }
                            }
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_download),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Download")
                            }
                        }
                    }
                    else {
                        Spacer(Modifier.width(8.dp))
                        Button(
                            enabled = !isSharing && isShareMode,
                            onClick = {
                                scope.launch {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    onShare(bitmap)
                                }
                            }
                        ) {
                            if (isSharing) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_share),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ShareMemoryCardDialogPreview() {
    MemoriesTheme {
        ShareMemoryCardDialog(
            memory = MemoryWithMediaModel(
                memory = MemoryModel(
                    title = "Really Emotional title bitch",
                    content = "Really long content"
                ),
            ),
            isShareMode = true,
            isSharing = true,
            isDownloading = false,
            onShare = {},
            onDownload = {},
            onDismiss = {}
        )
    }
}
