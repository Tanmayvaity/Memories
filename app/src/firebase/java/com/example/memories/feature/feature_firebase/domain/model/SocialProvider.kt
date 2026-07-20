package com.example.memories.feature.feature_firebase.domain.model

import androidx.annotation.DrawableRes
import com.example.memories.R

enum class SocialProvider(
    val displayName: String,
    @param:DrawableRes val iconRes: Int,
    val tintWithTheme: Boolean,
) {
    GOOGLE("Google", R.drawable.ic_google, tintWithTheme = false),
    FACEBOOK("Facebook", R.drawable.ic_facebook, tintWithTheme = false),
    GITHUB("GitHub", R.drawable.ic_github, tintWithTheme = true);

    /** Kept here so the width reservation and the rendered text can't drift apart. */
    val buttonLabel: String get() = "Continue with $displayName"
}
