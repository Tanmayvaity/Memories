package com.example.memories.core.presentation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.memories.core.presentation.components.PermissionDeniedSheet
import com.example.memories.core.util.createSettingsIntent

data class PermissionResult(
    val granted: List<String>,
    val denied: List<String>,
)

fun interface PermissionDeniedHandler {
    fun onDenied(denied: List<String>)
}

// Compose-scoped requester
@Composable
fun rememberPermissionRequester(
    onGranted: () -> Unit,
    onDenied: PermissionDeniedHandler = rememberPermissionDeniedHandler(),
): (Array<String>) -> Unit {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys.toList()
        if (denied.isEmpty()) onGranted()
        else onDenied.onDenied(denied)
    }
    return remember { { perms -> launcher.launch(perms) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberPermissionDeniedHandler(): PermissionDeniedHandler {
    var deniedPermissions by remember { mutableStateOf<List<String>?>(null) }
    val context = LocalContext.current

    deniedPermissions?.let { denied ->
        PermissionDeniedSheet(
            message = messageFor(denied),
            onDismiss = { deniedPermissions = null },
            onOpenSettings = {
                deniedPermissions = null
                createSettingsIntent(context)
            },
        )
    }

    return remember { PermissionDeniedHandler { denied -> deniedPermissions = denied } }
}

enum class AppPermission(
    val manifest: String,
    val message: String,
) {
    CAMERA(
        Manifest.permission.CAMERA,
        "Camera access is needed to capture your memories.",
    ),
    RECORD_AUDIO(
        Manifest.permission.RECORD_AUDIO,
        "Microphone access is needed to record video with sound.",
    );

    companion object {
        fun from(manifest: String): AppPermission? =
            entries.find { it.manifest == manifest }
    }
}

private fun messageFor(denied: List<String>): String =
    denied.singleOrNull()?.let { AppPermission.from(it)?.message }
        ?: "This feature needs access to continue. Enable it in your system settings."


fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermission(permission: AppPermission): Boolean =
    hasPermission(permission.manifest)

@Composable
fun rememberHasPermission(permission: String): Boolean {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(context.hasPermission(permission)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = context.hasPermission(permission)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return granted
}