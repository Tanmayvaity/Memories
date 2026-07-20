package com.example.memories.feature.feature_firebase.domain.model

enum class AuthMode(
    val tabLabel: String,
    val title: String,
    val subHeading: String,
    val submitLabel: String,
    val footerPrompt: String,
    val footerAction: String,
) {
    LOGIN(
        tabLabel = "Sign in",
        title = "Welcome back",
        subHeading = "Sign in to sync your memories across devices",
        submitLabel = "Sign in",
        footerPrompt = "New here?",
        footerAction = "Create an account"
    ),
    REGISTER(
        tabLabel = "Create account",
        title = "Create your account",
        subHeading = "Back up your memories and pick up where you left off",
        submitLabel = "Create account",
        footerPrompt = "Already have an account?",
        footerAction = "Sign in"
    );

    fun toggled(): AuthMode = if (this == LOGIN) REGISTER else LOGIN
}
