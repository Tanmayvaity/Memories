package com.example.memories.feature.feature_other.presentation



import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import coil3.util.CoilUtils.result
import com.example.memories.feature.feature_other.domain.model.LockMethod
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


private const val TAG = "BiometricPromptManager"

class BiometricPromptManager(
    private val activity: AppCompatActivity
) {
    private val _biometricResultsChannel = Channel<BiometricResult>()
    val biometricResults = _biometricResultsChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        description: String,
        lockMethod : LockMethod
    ) {
        Log.d(TAG, "showBiometricPrompt called - title: $title, description: $description")

        val manager = BiometricManager.from(activity)

        val authenticators = when (lockMethod) {
            LockMethod.DEVICE_BIOMETRIC -> {
                if (Build.VERSION.SDK_INT >= 30) {
                    BIOMETRIC_STRONG or BIOMETRIC_WEAK
                } else {
                    BIOMETRIC_STRONG
                }
            }
            LockMethod.DEVICE_PATTERN -> {
                if (Build.VERSION.SDK_INT >= 30) {
                    DEVICE_CREDENTIAL
                } else {
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                }
            }
            else -> return // Custom PIN or None, no biometric needed
        }

//        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
//             BIOMETRIC_STRONG or DEVICE_CREDENTIAL
//        } else BIOMETRIC_STRONG

        Log.d(TAG, "SDK version: ${Build.VERSION.SDK_INT}, authenticators: $authenticators")

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)

        if(lockMethod ==  LockMethod.DEVICE_BIOMETRIC){
            promptInfo.setNegativeButtonText("Cancel")
        }

        val canAuthenticate = manager.canAuthenticate(authenticators)
        Log.d(TAG, "canAuthenticate result: $canAuthenticate")

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.w(TAG, "Biometric hardware unavailable")
                _biometricResultsChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.w(TAG, "No biometric hardware found on device")
                _biometricResultsChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.w(TAG, "No biometric credentials enrolled")
                _biometricResultsChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "Biometric authentication is available, proceeding with prompt")
            }
            else -> {
                Log.d(TAG, "Unknown canAuthenticate result: $canAuthenticate, proceeding anyway")
            }
        }

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, "Authentication error - code: $errorCode, message: $errString")
                    _biometricResultsChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "Authentication succeeded - type: ${result.authenticationType}")
                    _biometricResultsChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.w(TAG, "Authentication failed - biometric not recognized")
                    _biometricResultsChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )

        Log.d(TAG, "Showing biometric prompt...")
        prompt.authenticate(promptInfo.build())
    }
}



sealed interface BiometricResult {
    data object HardwareUnavailable : BiometricResult
    data object FeatureUnavailable : BiometricResult
    data class AuthenticationError(val error : String) : BiometricResult
    data object AuthenticationFailed: BiometricResult
    data object AuthenticationSuccess: BiometricResult
    data object AuthenticationNotSet: BiometricResult
}