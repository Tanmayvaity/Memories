package com.example.memories.feature.feature_firebase.presentation

/** One-time UI events from [FirebaseViewModel], consumed once by `FirebaseRoot`. */
sealed interface FirebaseUiEvent {
    /** A Toast, not a snackbar: it draws above the modal AuthSheet, so errors are visible while it's open. */
    data class ShowToast(val message: String) : FirebaseUiEvent

    /** Sign-in rejected with wrong email or password: shown inline on the auth fields. */
    data object InvalidCredentials : FirebaseUiEvent
}
