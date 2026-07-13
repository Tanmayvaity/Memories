package com.example.memories.feature.feature_onboarding.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.memories.LocalTheme
import com.example.memories.ui.theme.MemoriesTheme

/**
 * Shared host for onboarding previews. Provides both [MemoriesTheme] and [LocalTheme] — the
 * latter matters because [com.example.memories.feature.feature_onboarding.presentation.components.AmbientBackground]
 * reads it to pick its speck/glow intensities, and the composition-local default (light) would
 * otherwise leak into dark previews.
 */
@Composable
internal fun OnboardingPreviewSurface(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalTheme provides darkTheme) {
        MemoriesTheme(darkTheme = darkTheme) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                content = content
            )
        }
    }
}
