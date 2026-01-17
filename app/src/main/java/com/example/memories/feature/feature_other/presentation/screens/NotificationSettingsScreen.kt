package com.example.memories.feature.feature_other.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.LocalTheme
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit = {}
) {

    val isDark = LocalTheme.current
    val color = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
    val context = LocalContext.current
    Scaffold(
        topBar = {

            AppTopBar(
                showDivider = false,
                title = {
                    Text(
                        text = "Notification Settings",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack
            )

        },
        containerColor = MaterialTheme.colorScheme.background
//        containerColor = Color(0xFFF8F9FB) // Light grayish background for better contrast with cards
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Enable All Notifications
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                NotificationToggleRow(
                    title = "Enable All Notifications",
                    description = "Globally pause or resume all alerts",
                    initialValue = true,
                    showDivider = false
                )
            }

            // My Memories Section
            SectionHeader(title = "MY MEMORIES")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    NotificationToggleRow(
                        title = "Reminders",
                        description = "Get nudged to record a memory if you haven't in a while.",
                        initialValue = true
                    )
                    NotificationToggleRow(
                        title = "On This Day",
                        description = "See what happened on this date in previous years.",
                        initialValue = true,
                        showDivider = false
                    )
                }
            }

            // Social Section
//            SectionHeader(title = "SOCIAL")
//            Card(
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//            ) {
//                NotificationToggleRow(
//                    title = "Friend Updates",
//                    description = "Be notified when friends share new moments with you.",
//                    initialValue = true,
//                    icon = Icons.Default.Check,
//                    showDivider = false
//                )
//            }

            // Updates & Offers Section
            SectionHeader(title = "UPDATES & OFFERS")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = color),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    NotificationToggleRow(
                        title = "App Updates",
                        description = "Stay informed about new features and improvements.",
                        initialValue = false,
                        showDivider = false
                    )
//                    NotificationToggleRow(
//                        title = "Promotional Offers",
//                        description = "Exclusive deals on premium features or printing services.",
//                        initialValue = false,
//                        showDivider = false
//                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Manage in System Settings
            TextButton(
                onClick = {
                    createSettingsIntent(context)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Manage in System Settings",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun NotificationToggleRow(
    title: String,
    description: String,
    initialValue: Boolean,
    icon: ImageVector? = null,
    showDivider: Boolean = true
) {
    var isChecked by remember { mutableStateOf(initialValue) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFF00A2E8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f)
            )
        }
    }
}

@Preview(showBackground = true)
@PreviewDynamicColors
@Composable
fun NotificationSettingsScreenPreview() {
    MemoriesTheme {
        NotificationSettingsScreen()
    }
}
