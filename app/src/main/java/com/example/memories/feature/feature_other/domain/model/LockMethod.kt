package com.example.memories.feature.feature_other.domain.model

import com.example.memories.R

enum class LockMethod(
    val title: String,
    val description: String,
    val icon: Int
) {
    DEVICE_BIOMETRIC(title = "Device Biometrics","Fingerprint or Face Unlock",R.drawable.ic_biometric),
    DEVICE_PATTERN("Device Credentials","Pin,Pattern or Password",R.drawable.ic_pattern),
    CUSTOM_PIN("Custom PIN","Set your own PIN",R.drawable.ic_custom_app_pin),
    NONE("None","No lock",R.drawable.ic_none)
}

enum class LockDuration(
    val title : String
){
    IMMEDIATELY("Immediately"),
    ONE_MINUTE("After 1 minutes"),
    THIRTY_MINUTES("After 30 minutes")
}
