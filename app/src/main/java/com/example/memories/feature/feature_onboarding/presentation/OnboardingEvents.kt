package com.example.memories.feature.feature_onboarding.presentation

sealed class OnboardingEvents {
    data object NextPage : OnboardingEvents()
    data object PreviousPage : OnboardingEvents()
    data class PageChanged(val page: Int) : OnboardingEvents()
    data object SkipOnboarding : OnboardingEvents()
    data object CompleteOnboarding : OnboardingEvents()
    data class UpdateCameraPermission(val granted: Boolean) : OnboardingEvents()
    data class UpdateNotificationPermission(val granted: Boolean) : OnboardingEvents()
    data class UpdateAlarmPermission(val granted: Boolean) : OnboardingEvents()

    object ScheduleReminderNotifications : OnboardingEvents()
}
