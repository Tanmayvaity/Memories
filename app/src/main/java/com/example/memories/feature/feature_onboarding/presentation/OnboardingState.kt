package com.example.memories.feature.feature_onboarding.presentation

data class OnboardingState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 5,
    val cameraPermissionGranted: Boolean = false,
    val notificationPermissionGranted: Boolean = false,
    val alarmPermissionGranted: Boolean = false
)
