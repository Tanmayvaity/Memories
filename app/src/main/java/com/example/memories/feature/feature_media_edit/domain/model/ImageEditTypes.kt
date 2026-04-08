package com.example.memories.feature.feature_media_edit.domain.model

import android.R.attr.entries
import com.example.memories.R

enum class FilterType(val displayName: String) {
    ORIGINAL("Original"),
    GRAYSCALE("GrayScale"),
    SEPIA("Sepia"),
    INVERT("Invert"),
    VINTAGE("Vintage"),
    COOL_FADE("Cool Fade"),
}

enum class AdjustType(
    val adjustTypeName: String,
    val icon: Int,
    val defaultValue: Float,
    val min: Float,
    val max: Float
) {
    BRIGHTNESS("Brightness", R.drawable.ic_brightness_2, 0f, -100f, 100f),
    BLUR("Blur", R.drawable.ic_blur, 0f, 0f, 20f),
    CONTRAST("Contrast", R.drawable.ic_contrast, 0f, -100f, 100f),
    SATURATION("Saturation", R.drawable.ic_saturation, 0f, -100f, 100f),
    TEMPERATURE("Temperature", R.drawable.ic_warmth, 0f, -100f, 100f),
    FADE("Fade", R.drawable.ic_fade, 0f, 0f, 100f),
    VIGNETTE("Vignette", R.drawable.ic_vignette, 0f, 0f, 100f);
}