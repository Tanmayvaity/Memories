package com.example.memories.core.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner



private class PermissionUtil {

    interface PermissionAskListener{
        fun onGranted()
        fun onRequest()
        fun onRationale()
    }



    companion object{
        private const val TAG = "PermissionUtil"

        private fun showPermissionState(
            permission: String,
            context : Context
        ): PermissionState {

            when {
                isPermissionGranted(context,permission) -> {
                    return PermissionState.PERMISSION_GRANTED
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission
                ) -> {
                    return PermissionState.PERMISSION_RATIONALE
                }

                else -> {
                    return PermissionState.PERMISSION_REQUEST
                }
            }


        }
        fun handlePermission(
            permission : String,
            context : Context,
            permissionAskListener: PermissionAskListener
        ){
            val state = showPermissionState(
                permission,
                context,
            )

            when(state){
                PermissionState.PERMISSION_GRANTED -> {
                    permissionAskListener.onGranted()
                }
                PermissionState.PERMISSION_RATIONALE -> {
                    permissionAskListener.onRationale()
                }
                PermissionState.PERMISSION_REQUEST -> {
                    permissionAskListener.onRequest()
                }

            }
        }
    }
    enum class PermissionState {
        PERMISSION_REQUEST,
        PERMISSION_GRANTED,
        PERMISSION_RATIONALE
    }
}


@Composable
fun PermissionHelper(
    lifecycleOwner : LifecycleOwner,
    onRationale : () -> Unit ,
    onGranted : () -> Unit,
    onRequest : (String) -> Unit,
    permission : String,
    context : Context
) {
    Log.d(TAG, "PermissionHelper: permission helper invoked")
    DisposableEffect(Unit) {
        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    Log.d(TAG, "PermissionHelper: inside event")
                    PermissionUtil.handlePermission(
                        permission = permission,
                        context = context,
                        object : PermissionUtil.PermissionAskListener {
                            override fun onGranted() {
                                Log.d(TAG, "PermissionHelper: granted")
                                onGranted.invoke()
                            }

                            override fun onRequest() {
                                Log.d(TAG, "PermissionHelper: request")
                                onRequest.invoke(permission)
                            }

                            override fun onRationale() {
                                Log.d(TAG, "PermissionHelper: rationale")
                                onRationale.invoke()
                            }

                        }
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


