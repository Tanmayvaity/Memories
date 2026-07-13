    package com.example.memories.feature.feature_onboarding.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.feature.feature_onboarding.presentation.OnboardingPreviewSurface

/**
 * A single permission row on the last onboarding page. The whole card is the tap target while the
 * permission is outstanding; once granted it becomes inert and the trailing action swaps for a
 * filled check.
 */
@Composable
fun PermissionCard(
    iconRes: Int,
    title: String,
    subtitle: String,
    granted: Boolean,
    actionLabel: String,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAction: Boolean = true
) {
    val borderColor by animateColorAsState(
        targetValue = if (granted) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "permission_border"
    )
    val containerColor by animateColorAsState(
        targetValue = if (granted) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "permission_container"
    )

    // Tick haptic when the permission flips to granted — but not when the card first
    // composes already granted (e.g. returning to the page or after rotation).
    val haptics = LocalHapticFeedback.current
    var wasGranted by rememberSaveable { mutableStateOf(granted) }
    LaunchedEffect(granted) {
        if (granted && !wasGranted) {
            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        }
        wasGranted = granted
    }

    val interactive = !granted && showAction

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (interactive) Modifier.clickable(onClick = onGrantClick) else Modifier
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            AnimatedContent(
                targetState = granted,
                transitionSpec = {
                    (scaleIn(
                        initialScale = 0.6f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn()) togetherWith (scaleOut(targetScale = 0.8f) + fadeOut())
                },
                label = "permission_action"
            ) { isGranted ->
                if (isGranted) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Granted",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else if (showAction) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = actionLabel,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionCardPreviewContent() {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionCard(
            iconRes = R.drawable.ic_camera,
            title = "Camera",
            subtitle = "Capture photos and videos",
            granted = false,
            actionLabel = "Allow",
            onGrantClick = {}
        )
        PermissionCard(
            iconRes = R.drawable.ic_notification,
            title = "Notifications",
            subtitle = "Reminders and daily highlights",
            granted = false,
            actionLabel = "Settings",
            onGrantClick = {}
        )
        PermissionCard(
            iconRes = R.drawable.ic_time,
            title = "Exact alarms",
            subtitle = "Schedule precise reminders",
            granted = true,
            actionLabel = "Settings",
            onGrantClick = {}
        )
    }
}

@Preview(name = "Permission states - dark")
@Composable
private fun PermissionCardDarkPreview() {
    OnboardingPreviewSurface(darkTheme = true) {
        PermissionCardPreviewContent()
    }
}

@Preview(name = "Permission states - light")
@Composable
private fun PermissionCardLightPreview() {
    OnboardingPreviewSurface(darkTheme = false) {
        PermissionCardPreviewContent()
    }
}
