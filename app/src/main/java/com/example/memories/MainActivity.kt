package com.example.memories

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.ui.theme.MemoriesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewmodel = hiltViewModel<ThemeViewModel>()
            val isDarkModeEnabled by viewmodel.isDarkModeEnabled.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            LaunchedEffect(isDarkModeEnabled) {
                Log.d("MainActivity", "onCreate: isDarkModeEnabled : ${isDarkModeEnabled}")
            }
            AnimatedContent(
                targetState = isDarkModeEnabled,
                transitionSpec = {
                    expandIn(
                        expandFrom = Alignment.TopStart,
                        animationSpec = tween(durationMillis = 500)
                    ) with fadeOut(
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                label = "ThemeTransition"
            ) { isDark ->
                MemoriesTheme(
                    darkTheme = isDark
                ) {

                    AppNav(navController)
                }

            }

        }
    }
}

@Preview(
    showBackground = true,
)
@PreviewLightDark
@Composable
fun AppNavPreview() {
    MemoriesTheme {
        AppNav(navController = rememberNavController())
    }
}




