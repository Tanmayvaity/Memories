package com.example.memories.feature.feature_onboarding.presentation

import androidx.annotation.DrawableRes
import com.example.memories.R

data class OnboardingPage(
    @param:DrawableRes val iconRes: Int,
    val title: String,
    val subtitle: String
)

/** Pages 1..3 of the pager. Page 0 is the welcome screen, page 4 is the permissions screen. */
val onboardingFeaturePages = listOf(
    OnboardingPage(
        iconRes = R.drawable.ic_camera,
        title = "Capture",
        subtitle = "Take photos and videos without ever leaving the app, then polish them with built-in filters."
    ),
    OnboardingPage(
        iconRes = R.drawable.ic_tag,
        title = "Organize",
        subtitle = "Tag your memories and search across everything you've saved to find any moment instantly."
    ),
    OnboardingPage(
        iconRes = R.drawable.ic_notification,
        title = "Remember",
        subtitle = "Gentle nudges to write things down, and quiet reminders of what happened on this day."
    )
)
