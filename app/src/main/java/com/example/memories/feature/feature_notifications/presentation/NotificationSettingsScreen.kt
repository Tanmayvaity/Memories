package com.example.memories.feature.feature_notifications.presentation

import android.Manifest
import android.R.attr.end
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.data.data_source.notification.NotificationService
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_notifications.presentation.components.ReminderTimePickerDialog
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun NotificationSettingsRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewmodel: NotificationsScreenViewModel = hiltViewModel()
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()

    NotificationSettingsScreen(
        onBack = onBack,
        state = state,
        onEvent = viewmodel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit = {},
    state: NotificationsScreenState = NotificationsScreenState(),
    onEvent: (NotificationsEvents) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var canScheduleExactAlarms by remember { mutableStateOf(false) }
    var isCheckingPermission by remember { mutableStateOf(true) }
    var isDailyReminderChannelAllowed by remember { mutableStateOf(true) }
    var isOnThisDayChannelAllowed by remember { mutableStateOf(true) }
    var isNotificationPermissionAllowed by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                canScheduleExactAlarms = canScheduleExactAlarmsCompat(context)
                isDailyReminderChannelAllowed = isChannelAllowed(context, NotificationService.DAILY_REMINDER_CHANNEL)
                isOnThisDayChannelAllowed = isChannelAllowed(context, NotificationService.ON_THIS_DAY_CHANNEL)
                isCheckingPermission = false
                isNotificationPermissionAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                } else true

                if(!isNotificationPermissionAllowed){
                    onEvent(NotificationsEvents.SetAllNotifications(false))
                    return@LifecycleEventObserver
                }

                if(!isDailyReminderChannelAllowed || !canScheduleExactAlarms){
                    onEvent(NotificationsEvents.SetReminderNotification(false))
                }
                if(!isOnThisDayChannelAllowed){
                    onEvent(NotificationsEvents.SetOnThisDayNotification(false))
                }

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                title = {
                    Text(
                        text = "Notification Settings",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showAction = true,
                actionContent = {
                    AnimatedVisibility(isCheckingPermission){
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp).padding(end = 3.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    }

                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
            NotificationCard(containerColor = cardColor) {
                NotificationToggleRow(
                    title = "Enable All Notifications",
                    description = "Globally pause or resume all alerts",
                    checked = state.allNotificationsEnabled,
                    showDivider = false,
                    onCheckedChange = { onEvent(NotificationsEvents.SetAllNotifications(it)) },
                    enabled = isNotificationPermissionAllowed && isOnThisDayChannelAllowed && isDailyReminderChannelAllowed && !isCheckingPermission
                )
            }

            // My Memories Section
            SectionHeader(title = "MY MEMORIES")
            NotificationCard(containerColor = cardColor) {
                NotificationToggleRow(
                    title = "Reminders",
                    description = "Get nudged to record a memory if you haven't in a while.",
                    checked = state.reminderNotificationEnabled,
                    enabled = !isCheckingPermission && canScheduleExactAlarms && isDailyReminderChannelAllowed && isNotificationPermissionAllowed,
                    showTimeChip = true,
                    timeChipValue = formatTime(state.reminderHour, state.reminderMinute),
                    onTimeChipClick = { showTimePicker = true },
                    showPermissionAlert = !isCheckingPermission && !canScheduleExactAlarms,
                    onCheckedChange = { onEvent(NotificationsEvents.SetReminderNotification(it)) }
                )
                NotificationToggleRow(
                    title = "On This Day",
                    description = "See what happened on this date in previous years.",
                    checked = state.onThisDayNotificationEnabled,
                    showDivider = false,
                    enabled  = isNotificationPermissionAllowed && isOnThisDayChannelAllowed && !isCheckingPermission,
                    onCheckedChange = { onEvent(NotificationsEvents.SetOnThisDayNotification(it)) }
                )
            }

            // Updates & Offers Section
            SectionHeader(title = "UPDATES & OFFERS")
            NotificationCard(containerColor = cardColor) {
                NotificationToggleRow(
                    title = "App Updates",
                    description = "Stay informed about new features and improvements.",
                    checked = false,
                    showDivider = false,
                    onCheckedChange = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(!isNotificationPermissionAllowed && !isCheckingPermission){
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning),
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Notification Permission has not been granted",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.weight(1f))


            ManageInSystemSettingsButton(
                onClick = { createSettingsIntent(context) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showTimePicker) {
            ReminderTimePicker(
                initialHour = state.reminderHour,
                initialMinute = state.reminderMinute,
                onDismiss = { showTimePicker = false },
                onConfirm = { hour, minute ->
                    onEvent(NotificationsEvents.SetReminderTime(hour, minute))
                    showTimePicker = false
                }
            )
        }
    }
}

// region Components

@Composable
private fun NotificationCard(
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column { content() }
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
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    showDivider: Boolean = true,
    showTimeChip: Boolean = false,
    timeChipValue: String = "",
    onTimeChipClick: () -> Unit = {},
    showPermissionAlert: Boolean = false
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = if (showTimeChip) 0.dp else 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TitleRow(icon = icon, title = title)
                Spacer(modifier = Modifier.height(4.dp))
                DescriptionText(description = description)

                AnimatedVisibility(showPermissionAlert) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PermissionAlert()
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }

        if (showTimeChip) {
            TimeChip(
                value = timeChipValue,
                enabled = enabled,
                onClick = onTimeChipClick
            )
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun TitleRow(icon: ImageVector?, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            Icon(
                imageVector = it,
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
}

@Composable
private fun DescriptionText(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        lineHeight = 16.sp
    )
}

@Composable
private fun TimeChip(
    value: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick,
        enabled = enabled,
        label = { Text(text = value) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_timer),
                contentDescription = "Reminder time"
            )
        }
    )
}

@Composable
private fun PermissionAlert() {
    val context = LocalContext.current

    val alertText = buildAnnotatedString {
        append("Exact alarm permission is required. ")
        withLink(
            LinkAnnotation.Clickable(
                tag = "fix",
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                )
            ) {
                openExactAlarmSettings(context)
            }
        ) {
            append("Tap to fix")
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_warning),
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = alertText,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun ManageInSystemSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    ReminderTimePickerDialog(
        onDismiss = onDismiss,
        onConfirm = { onConfirm(timePickerState.hour, timePickerState.minute) },
        timePickerState = timePickerState
    )
}

private fun canScheduleExactAlarmsCompat(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
    }
}

private fun isChannelAllowed(context : Context,channel : String) : Boolean{
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = notificationManager.getNotificationChannel(channel)

    return channel?.importance != NotificationManager.IMPORTANCE_NONE
}


@Preview(showBackground = true)
@PreviewDynamicColors
@Composable
fun NotificationSettingsScreenPreview() {
    MemoriesTheme {
        NotificationSettingsScreen()
    }
}