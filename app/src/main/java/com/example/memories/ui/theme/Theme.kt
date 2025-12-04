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
import androidx.compose.ui.tooling.preview.Preview

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

//private val customLightColorScheme = lightColorScheme(
//    primary = Color(0xff002d55),
//    secondary = Color(0xff222d3c),
//    tertiary = Color(0xff382442),
//    error = Color(0xff600004),
//    onPrimary = Color(0xffffffff),
//    onSecondary = Color(0xffffffff),
//    onTertiary = Color(0xffffffff),
//    onError = Color(0xffffffff),
//    primaryContainer = Color(0xff234a78),
//    secondaryContainer = Color(0xff3f4a5b),
//    tertiaryContainer = Color(0xff574160),
//    errorContainer = Color(0xff98000a),
//    onPrimaryContainer = Color(0xffffffff),
//    onSecondaryContainer = Color(0xffffffff),
//    onTertiaryContainer = Color(0xffffffff),
//    onErrorContainer = Color(0xffffffff),
//    surface = Color(0xfff8f9ff),
//    onSurface = Color(0xff000000),
//    surfaceVariant = Color(0xff000000),
//    inverseOnSurface = Color(0xffffffff),
//    inversePrimary = Color(0xffa4c9fe),
//    background = Color(0xffffffff),
//    surfaceContainer = Color(0xffeff0f7)
//)

private val customDarkColorScheme = lightColorScheme(

)


@Composable
fun MemoriesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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