package com.example.memories.feature.feature_media_edit.domain.model

import com.example.memories.R

enum class FilterType(val displayName: String) {
    ORIGINAL("Original"),
    GRAYSCALE("GrayScale"),
    SEPIA("Sepia"),
    INVERT("Invert"),
    VINTAGE("Vintage"),
    COOL_FADE("Cool Fade"),
//    ADEN("Aden")
}

enum class AdjustType(
    val displayName: String,
    val iconRes: Int,
    val defaultValue: Float,
    val minValue: Float,
    val maxValue: Float
) {
    BRIGHTNESS("Brightness", R.drawable.ic_brightness_2, 0f, -100f, 100f),
    BLUR("Blur", R.drawable.ic_blur, 0f, 0f, 20f),
    CONTRAST("Contrast", R.drawable.ic_contrast, 0f, -100f, 100f),
    SATURATION("Saturation", R.drawable.ic_saturation, 0f, -100f, 100f),
    TEMPERATURE("Temperature", R.drawable.ic_warmth, 0f, -100f, 100f),  // More subtle range
    FADE("Fade", R.drawable.ic_fade, 0f, 0f, 100f),
    VIGNETTE("Vignette", R.drawable.ic_vignette, 0f, 0f, 100f),
}