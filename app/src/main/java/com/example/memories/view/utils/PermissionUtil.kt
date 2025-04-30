package com.example.memories.view.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat

class PermissionUtil {

    interface PermissionAskListener{
        fun onGranted()
        fun onRequest()
        fun onRationale()
    }



    companion object{

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
}


enum class PermissionState {
    PERMISSION_REQUEST,
    PERMISSION_GRANTED,
    PERMISSION_RATIONALE
}