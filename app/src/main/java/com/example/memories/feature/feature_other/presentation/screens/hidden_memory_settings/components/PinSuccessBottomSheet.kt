package com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.feature.feature_other.presentation.screens.components.InfoBottomSheet
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSuccessBottomSheet(
    onDismiss: () -> Unit
) {

    InfoBottomSheet(
        onDismiss = onDismiss,
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        title = "PIN Set Successfully",
        subtitle = "Your hidden memories are now secured with\nyour private code.",
        primaryButtonText = "Done",
        onPrimaryClick = onDismiss,
        footerText = "You can change this in settings anytime"
    )





//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    ModalBottomSheet(
//        onDismissRequest = onDismiss,
//        sheetState = sheetState,
//        containerColor = MaterialTheme.colorScheme.surface,
//
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 24.dp)
//                .padding(bottom = 32.dp)
//        ) {
//            // Checkmark Icon
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .size(72.dp)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
//            ) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .size(42.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 1f))
//                ){
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = "Success",
//                        tint = MaterialTheme.colorScheme.onPrimary,
//                    )
//                }
//
//
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Title
//            Text(
//                text = "PIN Set Successfully",
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Subtitle
//            Text(
//                text = "Your hidden memories are now secured with\nyour private code.",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Done Button
//            Button(
//                onClick = onDismiss,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp)
//            ) {
//                Text(
//                    text = "Done",
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Footer text
//            Text(
//                text = "You can change this in settings anytime",
//                style = MaterialTheme.typography.labelSmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
//                letterSpacing = 1.sp
//            )
//        }
//    }
}


@Preview
@Composable
private fun PinSuccessBottomSheetPreview() {
    MemoriesTheme {
        PinSuccessBottomSheet(
            onDismiss = {}
        )
    }
}