package com.example.memories.feature.feature_onboarding.presentation

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.util.PhonePreview
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.feature.feature_onboarding.presentation.components.AmbientBackground
import com.example.memories.feature.feature_onboarding.presentation.components.MemoryCardStack
import com.example.memories.feature.feature_onboarding.presentation.components.OnboardingHero
import com.example.memories.feature.feature_onboarding.presentation.components.PageIndicator
import com.example.memories.feature.feature_onboarding.presentation.components.PermissionCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val WELCOME_PAGE = 0
private const val PERMISSIONS_PAGE = 4

private val CollapsedButtonSize = 60.dp

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
    val isLastPage by remember(state.totalPages) {
        derivedStateOf { pagerState.currentPage == state.totalPages - 1 }
    }

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

    // A soft tick every time the pager lands on a page; drop(1) skips the initial settle.
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .drop(1)
            .collect { haptics.performHapticFeedback(HapticFeedbackType.SegmentTick) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AmbientBackground(
                modifier = Modifier.fillMaxSize(),
                fraction = { pagerState.currentPage + pagerState.currentPageOffsetFraction }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TopBar(
                    showBack = pagerState.currentPage > WELCOME_PAGE,
                    showSkip = !isLastPage,
                    onBack = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                    },
                    onSkip = { onEvent(OnboardingEvents.SkipOnboarding) }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    // 0 at rest, ±1 when a full page away. Drives every parallax layer below.
                    val parallax = {
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    }

                    when (page) {
                        WELCOME_PAGE -> WelcomePage(parallax = parallax)

                        PERMISSIONS_PAGE -> PermissionsPage(
                            state = state,
                            onEvent = onEvent
                        )

                        else -> FeaturePage(
                            page = onboardingFeaturePages[page - 1],
                            parallax = parallax
                        )
                    }
                }

                BottomBar(
                    totalPages = state.totalPages,
                    fraction = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
                    isLastPage = isLastPage,
                    onNext = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    onFinish = {
                        onEvent(OnboardingEvents.CompleteOnboarding)
                        onEvent(OnboardingEvents.ScheduleReminderNotifications)
                    }
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    showBack: Boolean,
    showSkip: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(visible = showBack, enter = fadeIn(), exit = fadeOut()) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous page",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedVisibility(visible = showSkip, enter = fadeIn(), exit = fadeOut()) {
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    totalPages: Int,
    fraction: () -> Float,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // On the last page the button swallows the whole row and the indicator fades out from
        // underneath it.
        val buttonWidth by animateDpAsState(
            targetValue = if (isLastPage) maxWidth else CollapsedButtonSize,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            label = "button_width"
        )
        val cornerRadius by animateDpAsState(
            targetValue = if (isLastPage) 20.dp else CollapsedButtonSize / 2,
            label = "button_corner"
        )
        val indicatorAlpha by animateFloatAsState(
            targetValue = if (isLastPage) 0f else 1f,
            label = "indicator_alpha"
        )

        PageIndicator(
            totalPages = totalPages,
            fraction = fraction,
            modifier = Modifier.graphicsLayer { alpha = indicatorAlpha }
        )

        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val pressScale by animateFloatAsState(
            targetValue = if (pressed) 0.92f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "press_scale"
        )

        val arcColor = MaterialTheme.colorScheme.primary
        val trackColor = MaterialTheme.colorScheme.outlineVariant

        Button(
            onClick = { if (isLastPage) onFinish() else onNext() },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(buttonWidth)
                .height(CollapsedButtonSize)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                // Progress ring floating just outside the collapsed button: a faint full-circle
                // track plus an arc that fills as pages advance. Anchored to the right cap so it
                // stays put while the button stretches, and it fades with the page indicator.
                .drawBehind {
                    if (indicatorAlpha > 0.01f) {
                        val gap = 5.dp.toPx()
                        val strokeWidth = 2.dp.toPx()
                        val ringRadius = size.height / 2f + gap
                        val ringCenter = Offset(size.width - size.height / 2f, size.height / 2f)

                        drawCircle(
                            color = trackColor.copy(alpha = 0.45f * indicatorAlpha),
                            radius = ringRadius,
                            center = ringCenter,
                            style = Stroke(width = strokeWidth * 0.75f)
                        )

                        val progress = ((fraction() + 1f) / totalPages).coerceIn(0f, 1f)
                        drawArc(
                            color = arcColor.copy(alpha = indicatorAlpha),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = Offset(ringCenter.x - ringRadius, ringCenter.y - ringRadius),
                            size = Size(ringRadius * 2f, ringRadius * 2f),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                },
            shape = RoundedCornerShape(cornerRadius),
            interactionSource = interactionSource,
            // The collapsed button is only 60.dp wide; default padding would squeeze out the icon.
            contentPadding = PaddingValues(0.dp)
        ) {
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "button_label"
            ) { finishing ->
                if (finishing) {
                    Text(
                        text = "Get Started",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next page",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomePage(parallax: () -> Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val stackEntrance = rememberEntrance(delayMillis = 80)
        val titleEntrance = rememberEntrance(delayMillis = 320)
        val subtitleEntrance = rememberEntrance(delayMillis = 460)

        MemoryCardStack(
            iconRes = R.drawable.ic_memory,
            parallax = parallax,
            modifier = Modifier.entrance { stackEntrance.value }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Text trails behind the card stack on swipe, same treatment as the feature pages.
        Column(
            modifier = Modifier.graphicsLayer {
                translationX = parallax() * size.width * 0.45f
                alpha = (1f - abs(parallax()) * 1.6f).coerceIn(0f, 1f)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val titleColor = MaterialTheme.colorScheme.onBackground
            Text(
                text = "Memories",
                modifier = Modifier.entrance { titleEntrance.value },
                // Vertical fade through the glyphs — bright at the top, dissolving toward the
                // baseline — keeps the monochrome palette but gives the wordmark some depth.
                style = MaterialTheme.typography.displaySmall.merge(
                    TextStyle(
                        brush = Brush.verticalGradient(
                            colors = listOf(titleColor, titleColor.copy(alpha = 0.55f))
                        )
                    )
                ),
                fontSize = 44.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your personal journal,\ncaptured in moments.",
                modifier = Modifier.entrance { subtitleEntrance.value },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun FeaturePage(
    page: OnboardingPage,
    parallax: () -> Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingHero(
            iconRes = page.iconRes,
            parallax = parallax
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Text trails the hero and fades as the page leaves the viewport.
        Column(
            modifier = Modifier.graphicsLayer {
                translationX = parallax() * size.width * 0.45f
                alpha = (1f - abs(parallax()) * 1.6f).coerceIn(0f, 1f)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge,
                letterSpacing = (-0.5).sp,
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
}

@Composable
private fun PermissionsPage(
    state: OnboardingState,
    onEvent: (OnboardingEvents) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    // A permission is "blocked" once the system stops showing its dialog, at which point the only
    // way to grant it is through app settings.
    var cameraBlocked by rememberSaveable { mutableStateOf(false) }
    var notificationsBlocked by rememberSaveable { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onEvent(OnboardingEvents.UpdateCameraPermission(granted))
        if (!granted && activity?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) == false) {
            cameraBlocked = true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        onEvent(OnboardingEvents.UpdateNotificationPermission(granted))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !granted &&
            activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false
        ) {
            notificationsBlocked = true
        }
    }

    // Re-reads permissions on entry and every time we come back from system settings.
    LifecycleResumeEffect(Unit) {
        onEvent(
            OnboardingEvents.UpdateCameraPermission(
                context.checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onEvent(
                OnboardingEvents.UpdateNotificationPermission(
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_GRANTED
                )
            )
        } else {
            onEvent(OnboardingEvents.UpdateNotificationPermission(true))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            onEvent(OnboardingEvents.UpdateAlarmPermission(alarmManager.canScheduleExactAlarms()))
        } else {
            onEvent(OnboardingEvents.UpdateAlarmPermission(true))
        }

        onPauseOrDispose { }
    }

    val allGranted = state.cameraPermissionGranted &&
            state.notificationPermissionGranted &&
            state.alarmPermissionGranted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val headerEntrance = rememberEntrance(delayMillis = 80)
        val cameraEntrance = rememberEntrance(delayMillis = 240)
        val notificationEntrance = rememberEntrance(delayMillis = 340)
        val alarmEntrance = rememberEntrance(delayMillis = 440)
        val footnoteEntrance = rememberEntrance(delayMillis = 560)

        // One-shot sparkle burst behind the header the moment the last permission lands.
        val burst = remember { Animatable(1f) }
        val burstParticles = remember { List(18) { BurstParticle(Random(it * 131 + 17)) } }
        var celebrated by rememberSaveable { mutableStateOf(false) }
        LaunchedEffect(allGranted) {
            if (allGranted && !celebrated) {
                celebrated = true
                burst.snapTo(0f)
                burst.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                )
            }
        }
        val burstColor = MaterialTheme.colorScheme.primary

        Column(
            modifier = Modifier
                .entrance { headerEntrance.value }
                .graphicsLayer {
                    // Gentle pop that rises and settles over the course of the burst.
                    val pop = 1f + 0.05f * sin(burst.value * PI.toFloat())
                    scaleX = pop
                    scaleY = pop
                }
                .drawBehind {
                    val progress = burst.value
                    if (progress < 1f) {
                        val origin = Offset(size.width / 2f, size.height / 2f)
                        val fade = 1f - progress
                        burstParticles.forEach { particle ->
                            val distance = particle.distanceDp.dp.toPx() * progress
                            drawCircle(
                                color = burstColor.copy(alpha = 0.8f * fade),
                                radius = particle.radiusDp.dp.toPx() * (0.5f + 0.5f * fade),
                                center = origin + Offset(
                                    cos(particle.angle) * distance,
                                    sin(particle.angle) * distance
                                )
                            )
                        }
                    }
                }
        ) {
            Text(
                text = "Almost there",
                style = MaterialTheme.typography.headlineMedium,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = allGranted,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "permissions_subtitle"
            ) { granted ->
                Text(
                    text = if (granted) {
                        "You're all set. Everything's ready to go."
                    } else {
                        "These are optional — grant them to unlock every part of the app."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PermissionCard(
            iconRes = R.drawable.ic_camera,
            title = "Camera",
            subtitle = "Capture photos and videos",
            granted = state.cameraPermissionGranted,
            actionLabel = if (cameraBlocked) "Settings" else "Allow",
            onGrantClick = {
                if (cameraBlocked) {
                    createSettingsIntent(context)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.entrance { cameraEntrance.value }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionCard(
            iconRes = R.drawable.ic_notification,
            title = "Notifications",
            subtitle = "Reminders and daily highlights",
            granted = state.notificationPermissionGranted,
            actionLabel = if (notificationsBlocked) "Settings" else "Allow",
            onGrantClick = {
                if (notificationsBlocked) {
                    createSettingsIntent(context)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            modifier = Modifier.entrance { notificationEntrance.value },
            showAction = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        )

        Spacer(modifier = Modifier.height(12.dp))

        PermissionCard(
            iconRes = R.drawable.ic_time,
            title = "Exact alarms",
            subtitle = "Schedule precise reminders",
            granted = state.alarmPermissionGranted,
            actionLabel = "Settings",
            onGrantClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    try {
                        context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    } catch (_: ActivityNotFoundException) {
                        createSettingsIntent(context)
                    }
                }
            },
            modifier = Modifier.entrance { alarmEntrance.value },
            showAction = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "You can change these at any time from Settings.",
            modifier = Modifier
                .fillMaxWidth()
                .entrance { footnoteEntrance.value },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Runs a 0→1 progress once, after [delayMillis], for staggering a page's content in. */
@Composable
private fun rememberEntrance(delayMillis: Int): State<Float> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
        )
    }
    return progress.asState()
}

/** Fades, lifts, and slightly scales content into place using a progress from [rememberEntrance]. */
private fun Modifier.entrance(progress: () -> Float): Modifier = graphicsLayer {
    val value = progress()
    alpha = value
    translationY = (1f - value) * 32.dp.toPx()
    val scale = 0.94f + 0.06f * value
    scaleX = scale
    scaleY = scale
}

/** A single mote of the all-permissions-granted celebration burst. */
private class BurstParticle(random: Random) {
    val angle = random.nextFloat() * 2f * PI.toFloat()
    val distanceDp = 40f + random.nextFloat() * 60f
    val radiusDp = 1.2f + random.nextFloat() * 1.8f
}

// --- Previews ---------------------------------------------------------------------------------

@PhonePreview
@Composable
private fun OnboardingScreenWelcomeDarkPreview() {
    OnboardingPreviewSurface(darkTheme = true) {
        OnboardingScreen(
            state = OnboardingState(isLoading = false, currentPage = 0),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenWelcomeLightPreview() {
    OnboardingPreviewSurface(darkTheme = false) {
        OnboardingScreen(
            state = OnboardingState(isLoading = false, currentPage = 0),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenFeaturePreview() {
    OnboardingPreviewSurface {
        OnboardingScreen(
            state = OnboardingState(isLoading = false, currentPage = 1),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenPermissionsPreview() {

    OnboardingPreviewSurface {
        OnboardingScreen(
            state = OnboardingState(
                isLoading = false,
                currentPage = 4,
                cameraPermissionGranted = true
            ),
            onEvent = {}
        )
    }
}
