package com.example.memories.feature.feature_other.domain.model

import com.example.memories.R

enum class LockMethod(
    val title: String,
    val description: String,
    val icon: Int
) {
    DEVICE_PIN(title = "PIN","Your device pin",R.drawable.ic_pin),
    DEVICE_PATTERN("Pattern","Connect dots to unlock",R.drawable.ic_pattern),
    FINGERPRINT("Fingerprint","Use your fingerprint",R.drawable.ic_biometric),
    FACE_ID("Face Unlock","Use your face",R.drawable.ic_face_unlock),
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
