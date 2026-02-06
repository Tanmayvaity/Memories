package com.example.memories

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.memories.core.data.data_source.OtherSettingsDatastore
import com.example.memories.core.domain.usecase.InvokeNotificationUseCase
import com.example.memories.core.presentation.ThemeEvents
import com.example.memories.core.presentation.ThemeViewModel
import com.example.memories.core.presentation.components.GeneralAlertDialog
import com.example.memories.core.presentation.components.GeneralAlertSheet
import com.example.memories.core.util.createSettingsIntent
import com.example.memories.core.util.hasPostNotificationPermission
import com.example.memories.core.util.isPermissionGranted
import com.example.memories.feature.feature_other.presentation.ThemeTypes
import com.example.memories.ui.theme.MemoriesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


val LocalTheme = compositionLocalOf { false }



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var invokeNotificationUseCase: InvokeNotificationUseCase

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewmodel = hiltViewModel<ThemeViewModel>()
            val isDarkModeEnabled by viewmodel.isDarkModeEnabled.collectAsStateWithLifecycle()
            val lifecycleOwner = LocalLifecycleOwner.current
            val navController = rememberNavController()
            var showPermissionDeniedDialog by remember {
                mutableStateOf(false)
            }
            val context = LocalContext.current
            val notificationLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
            ) { isGranted ->

                if(isGranted){
                    CoroutineScope(Dispatchers.IO).launch {
                        invokeNotificationUseCase()
                    }
                }

            }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> {
//                            requestPermissionLogic(
//                                context,
//                                Manifest.permission.POST_NOTIFICATIONS,
//                                onGranted = {
//
//                                },
//                                notificationLauncher
//                            )

                            if(hasPostNotificationPermission()){
                                CoroutineScope(Dispatchers.IO).launch {
                                    invokeNotificationUseCase()
                                }
                            }
                        }

                        Lifecycle.Event.ON_STOP -> onStop()
                        else -> {}
                    }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }

            }
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
                val themeState = (isDark == ThemeTypes.DARK)
                CompositionLocalProvider(
                    LocalTheme provides themeState
                ) {
                    MemoriesTheme(
                        darkTheme = themeState
                    ) {

                        AppNav(navController)
                    }
                }

            }
            if (showPermissionDeniedDialog) {
                GeneralAlertDialog(
                    title = "Permission Denied",
                    text = "Please grant the permission to use the reminder/on this day feature",
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismiss = {
                        showPermissionDeniedDialog = false
                    },
                    onConfirm = {

                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                createSettingsIntent(context)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = "Settings"
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showPermissionDeniedDialog = false
                            }
                        ) {
                            Text(
                                text = "Dismiss"
                            )
                        }
                    }

                )
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


private fun requestPermissionLogic(
    context: Context,
    permission: String,
    onGranted: () -> Unit = {},
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val activity = context as Activity
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
            onGranted()
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isPermissionGranted(
            context,
            permission
        ) -> {
            onGranted()
        }

        activity.shouldShowRequestPermissionRationale(
            permission
        ) -> {}

        else -> {
            launcher.launch(permission)
        }
    }
}




