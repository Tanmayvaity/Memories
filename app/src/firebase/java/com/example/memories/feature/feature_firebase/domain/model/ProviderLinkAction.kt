package com.example.memories.feature.feature_firebase.domain.model

/**
 * What a tap on a sign-in method row is asking for. Linking a provider to the signed-in account
 * means either sign-in method lands on the same user, so both directions share one confirm sheet.
 */
enum class ProviderLinkAction(
    val confirmLabel: String,
    val isDestructive: Boolean,
) {
    CONNECT(confirmLabel = "Connect", isDestructive = false),
    DISCONNECT(confirmLabel = "Disconnect", isDestructive = true);
}
