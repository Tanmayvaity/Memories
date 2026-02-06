package com.example.memories.feature.feature_onboarding.presentation

import android.Manifest
import android.R.attr.contentDescription
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.feature.feature_onboarding.presentation.components.PageIndicator
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoot(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isOnboardingCompleted, state.isLoading) {
        if (!state.isLoading && state.isOnboardingCompleted) {
            onOnboardingComplete()
        }
    }

    if (state.isLoading || state.isOnboardingCompleted) {
        return
    }

    OnboardingScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onEvent: (OnboardingEvents) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.totalPages }
    )
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == state.totalPages - 1

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onEvent(OnboardingEvents.PageChanged(page))
        }
    }

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isLastPage) {
                    TextButton(
                        onClick = { onEvent(OnboardingEvents.SkipOnboarding) }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> FeaturePage(
                        page = OnboardingPage(
                            icon = Icons.Outlined.AccountBox,
                            title = "Capture",
                            subtitle = "Take photos and videos directly within the app using the built-in camera."
                        )
                    )

                    2 -> FeaturePage(
                        page = OnboardingPage(
                            icon = Icons.Outlined.DateRange,
                            title = "Organize",
                            subtitle = "Tag and search your memories to find them instantly when you need them."
                        )
                    )

                    3 -> FeaturePage(
                        page = OnboardingPage(
                            icon = Icons.Outlined.Notifications,
                            title = "Reminders",
                            subtitle = "Get gentle nudges to capture moments and revisit memories from the past."
                        )
                    )

                    4 -> PermissionsPage(
                        state = state,
                        onEvent = onEvent
                    )
                }
            }

            // Bottom area: indicator + button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PageIndicator(
                    totalPages = state.totalPages,
                    currentPage = pagerState.currentPage
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isLastPage) {
                            onEvent(OnboardingEvents.CompleteOnboarding)
                            onEvent(OnboardingEvents.ScheduleReminderNotifications)
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Get Started" else "Continue",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Memories",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Your personal journal,\ncaptured in moments.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun FeaturePage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = page.title,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun PermissionsPage(
    state: OnboardingState,
    onEvent: (OnboardingEvents) -> Unit
) {
    val context = LocalContext.current
    val activity  = context as Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onEvent(OnboardingEvents.UpdateCameraPermission(granted))
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onEvent(OnboardingEvents.UpdateNotificationPermission(granted))
    }

    // Re-check permissions when returning from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onEvent(
                    OnboardingEvents.UpdateCameraPermission(
                        context.checkSelfPermission(Manifest.permission.CAMERA) ==
                                android.content.pm.PackageManager.PERMISSION_GRANTED
                    )
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    onEvent(
                        OnboardingEvents.UpdateNotificationPermission(
                            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                                    android.content.pm.PackageManager.PERMISSION_GRANTED
                        )
                    )
                } else {
                    onEvent(OnboardingEvents.UpdateNotificationPermission(true))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    onEvent(OnboardingEvents.UpdateAlarmPermission(alarmManager.canScheduleExactAlarms()))
                } else {
                    onEvent(OnboardingEvents.UpdateAlarmPermission(true))
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Permissions",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Optional permissions to unlock all features.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        PermissionRow(
            icon = Icons.Outlined.AccountBox,
            title = "Camera",
            subtitle = "Capture photos and videos",
            granted = state.cameraPermissionGranted,
            onGrantClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionRow(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            subtitle = "Reminders and daily highlights",
            granted = state.notificationPermissionGranted,
            onGrantClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            showButton = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionRow(
            icon = Icons.Outlined.DateRange,
            title = "Exact Alarms",
            subtitle = "Schedule precise reminders",
            granted = state.alarmPermissionGranted,
            buttonText = "Open Settings",
            onGrantClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    context.startActivity(intent)
                }
            },
            showButton = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        )
    }
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    granted: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Grant",
    showButton: Boolean = true
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (granted) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = "Granted",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        } else if (showButton) {
            OutlinedButton(
                onClick = onGrantClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

//        IconButton(
//            onClick = {
//                createSettingsIntent(context)
//            },
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.Close,
//                contentDescription = "Not Granted",
//                tint = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.size(24.dp),
//            )
//        }
    }
}
