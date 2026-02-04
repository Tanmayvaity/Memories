package com.example.memories.feature.feature_backup.presentation

import android.Manifest
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.WavyProgressIndicatorDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.PermissionAlert
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.core.util.hasPostNotificationPermission
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_backup.presentation.components.BackupFrequencySheet
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun BackupRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackupScreen(
        onBack = onBack,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    state : BackupScreenState = BackupScreenState(),
    onEvent : (BackupEvents) -> Unit = {},
) {

    var showBackupFrequencySheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasNotificationPermission by remember { mutableStateOf(context.hasPostNotificationPermission()) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationPermission = context.hasPostNotificationPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    Scaffold(
        topBar = {
            AppTopBar(
                title = {
                    Text(
                        text = "Backup & Restore",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showDivider = false
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Google Drive Card
            AccountCard()

            // Last Backup Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "LAST BACKUP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LastBackupItem(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_calender,
                        value = "Oct 24",
                        label = "Date"
                    )
                    LastBackupItem(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_time,
                        value = "10:42 AM",
                        label = "Time"
                    )
                    LastBackupItem(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_storage,
                        value = "342 MB",
                        label = "Size"
                    )
                }
            }

            // Backup Button
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_database_backup),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Backup Now", fontWeight = FontWeight.Medium)
                }
            }

            // Progress Section
            ProgressCard()

            // Settings Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SettingCard(
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column {
                        SettingRow(
                            title = "Automatic Backup",
                            subtitle = "Backup your data automatically",
                            trailing = {
                                Switch(checked = true, onCheckedChange = {}, enabled = hasNotificationPermission)
                            },
                            showAlert = !hasNotificationPermission,
                            onTapToFixClick = {
                                createSettingsIntent(context)
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        SettingRow(
                            title = "Frequency",
                            onClick = {
                                showBackupFrequencySheet = true
                            },
                            trailing = {
                                Row(
                                    modifier = Modifier
                                        .noRippleClickable(
                                            onClick = {
                                                showBackupFrequencySheet = true
                                            }
                                        )
                                    ,

                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.backupFrequencyState.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.ic_right),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        SettingRow(
                            title = "Use Cellular Data",
                            subtitle = "Backup when Wi-Fi is unavailable",
                            trailing = {
                                Switch(checked = false, onCheckedChange = {})
                            }
                        )
                    }
                }
            }

            // Restore Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "RESTORE DATA",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RestoreCard()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    if(showBackupFrequencySheet){
        BackupFrequencySheet(
            sheetState = sheetState,
            defaultFrequency = state.backupFrequencyState,
            onApplyBackupFrequency = { frequency ->
                onEvent(BackupEvents.ChangeFrequencyType(frequency))
                showBackupFrequencySheet = false
            },
            onDismiss = {
                showBackupFrequencySheet = false
            }
        )
    }
}

@Composable
fun AccountCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_database_backup),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Google Drive",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Connected as john.doe@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(
                thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
//                    .padding(horizontal = 16.dp, vertical = 8.dp)
                        ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unlink Account",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource(R.drawable.ic_logout), // Placeholder
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LastBackupItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    value: String,
    label: String,
    iconColor : Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
//            Box(
//                modifier = Modifier
//                    .size(32.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(iconRes),
//                    contentDescription = null,
//                    modifier = Modifier.size(18.dp),
//                    tint = iconColor
//                )
//            }
            IconItem(
                drawableRes = iconRes,
                contentDescription = label,
                color = iconColor,
                alpha = 0f,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f),
                    gapSize = 0.dp,
                    progress = {
                     0.7f
                    },
                    stroke = Stroke(
                        width = 8.dp.value
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Backing up... 50%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "2m left",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Uploading 1,204 items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearWavyProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                ,
                progress = {
                    0.5f
                },
                gapSize = 0.dp,
            )

        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    onClick : () -> Unit = {},
    trailing: @Composable () -> Unit,
    showAlert : Boolean = false,
    onTapToFixClick : () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .noRippleClickable(
                onClick = onClick
            )
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(showAlert){
                Spacer(modifier = Modifier.height(8.dp))
                PermissionAlert(
                    initialText = "Notification permission is required for backups",
                    onTapToFixClick = onTapToFixClick
                )
            }
        }
        trailing()
    }
}

@Composable
fun RestoreCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Restore from Cloud",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This will overwrite your current local data with the latest backup from Oct 24, 2023. This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(text = "Restore from Backup", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackupScreenPreview() {
    MemoriesTheme {
        BackupScreen()
    }
}
