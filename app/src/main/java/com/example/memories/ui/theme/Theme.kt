package com.example.memories.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Theme - Clean monochrome with subtle warmth
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A1A),            // Near black
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0E0E0),   // Light gray
    onPrimaryContainer = Color(0xFF1A1A1A),

    secondary = Color(0xFF424242),          // Dark gray
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF1A1A1A),

    tertiary = Color(0xFF616161),           // Medium gray
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF5F5F5),
    onTertiaryContainer = Color(0xFF1A1A1A),

    error = Color(0xFFB00020),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFCD8DF),
    onErrorContainer = Color(0xFF370009),

    background = Color(0xFFFFFFFF),          // Pure white
    onBackground = Color(0xFF1A1A1A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF424242),

    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),

    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = Color(0xFFE0E0E0),

    surfaceTint = Color(0xFF1A1A1A),
    scrim = Color(0xFF000000),

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFAFA),
    surfaceContainer = Color(0xFFF5F5F5),
    surfaceContainerHigh = Color(0xFFF0F0F0),
    surfaceContainerHighest = Color(0xFFEBEBEB)
)

// Dark Theme - True black AMOLED style
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),            // Pure white accent
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2A2A2A),   // Dark gray container
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFFB0B0B0),          // Soft gray
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1F1F1F),
    onSecondaryContainer = Color(0xFFE0E0E0),

    tertiary = Color(0xFF8A8A8A),           // Medium gray
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF1A1A1A),
    onTertiaryContainer = Color(0xFFBDBDBD),

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF370009),
    onErrorContainer = Color(0xFFCF6679),

    background = Color(0xFF000000),          // True black (AMOLED)
    onBackground = Color(0xFFE5E5E5),

    surface = Color(0xFF000000),
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFB0B0B0),

    outline = Color(0xFF4A4A4A),
    outlineVariant = Color(0xFF2A2A2A),

    inverseSurface = Color(0xFFE5E5E5),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = Color(0xFF1A1A1A),

    surfaceTint = Color(0xFFFFFFFF),
    scrim = Color(0xFF000000),

    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0D0D0D),
    surfaceContainer = Color(0xFF121212),
    surfaceContainerHigh = Color(0xFF1A1A1A),
    surfaceContainerHighest = Color(0xFF242424)
)

@Composable
fun MemoriesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}