package com.example.memories.feature.feature_feed.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.domain.model.SyncSummary
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncStatusCard(
    summary: SyncSummary,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val count = summary.pendingCount
    val memories = if (count == 1) "memory" else "memories"
    val title = if (summary.isUploading) "Syncing $count $memories" else "$count $memories waiting to sync"
    val subtitle = if (summary.isUploading) "Uploading to your account…" else "Tap to see what's pending"

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SyncStatusBadge(isUploading = summary.isUploading)
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss sync status",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            if (summary.isUploading) {
                LinearWavyProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    color = LocalContentColor.current,
                    trackColor = LocalContentColor.current.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SyncStatusBadge(isUploading: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(LocalContentColor.current.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        if (isUploading) {
            CircularWavyProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = LocalContentColor.current,
                trackColor = LocalContentColor.current.copy(alpha = 0.2f),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_cloud_upload),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SyncStatusCardPendingPreview() {
    MemoriesTheme {
        SyncStatusCard(summary = SyncSummary(pendingCount = 12), onClick = {}, onDismiss = {})
    }
}

@PreviewLightDark
@Composable
private fun SyncStatusCardUploadingPreview() {
    MemoriesTheme {
        SyncStatusCard(
            summary = SyncSummary(pendingCount = 3, isUploading = true),
            onClick = {},
            onDismiss = {},
        )
    }
}
