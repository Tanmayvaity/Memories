package com.example.memories.feature.feature_firebase.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_feed.presentation.history.components.AnimatedSegmentedRow
import com.example.memories.feature.feature_firebase.presentation.AuthMode
import com.example.memories.feature.feature_firebase.domain.model.SocialProvider
import com.example.memories.ui.theme.MemoriesTheme

private const val MIN_PASSWORD_LENGTH = 6


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthSheet(
    modifier: Modifier = Modifier,
    authMode: AuthMode = AuthMode.LOGIN,
    onAuthModeChange: (AuthMode) -> Unit = {},
    isLoading: Boolean = false,
    // Sign-in rejected with wrong email or password: both fields go red until either is edited.
    isCredentialsError: Boolean = false,
    onCredentialsErrorCleared: () -> Unit = {},
    onSubmit: (mode: AuthMode, email: String, password: String) -> Unit = { _, _, _ -> },
    onSocialSignIn: (SocialProvider) -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var hasSubmitted by rememberSaveable { mutableStateOf(false) }

    val indicatorPosition by animateFloatAsState(
        targetValue = authMode.ordinal.toFloat(),
        label = "authModeIndicator"
    )

    val emailError = remember(email, hasSubmitted) {
        if (!hasSubmitted) null
        else when {
            email.isBlank() -> "Enter your email"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Enter a valid email address"

            else -> null
        }
    }
    val passwordError = remember(password, hasSubmitted, authMode) {
        if (!hasSubmitted) null
        else when {
            password.isBlank() -> "Enter your password"
            authMode == AuthMode.REGISTER && password.length < MIN_PASSWORD_LENGTH ->
                "Use at least $MIN_PASSWORD_LENGTH characters"

            else -> null
        }
    }
    val confirmPasswordError = remember(password, confirmPassword, hasSubmitted, authMode) {
        if (!hasSubmitted || authMode != AuthMode.REGISTER) null
        else if (confirmPassword != password) "Passwords don't match" else null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedSegmentedRow(
                selectedIndex = authMode.ordinal,
                options = AuthMode.entries.map { it.tabLabel },
                onSelect = { index ->
                    if (!isLoading) {
                        onAuthModeChange(AuthMode.entries[index])
                        hasSubmitted = false
                    }
                },
                pagerPosition = indicatorPosition,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = authMode.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = authMode.subHeading,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    onCredentialsErrorCleared()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Email") },
                singleLine = true,
                enabled = !isLoading,
                isError = emailError != null || isCredentialsError,
                supportingText = emailError?.let { { Text(text = it) } },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    onCredentialsErrorCleared()
                },
                label = "Password",
                enabled = !isLoading,
                isVisible = isPasswordVisible,
                onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                errorMessage = passwordError
                    ?: if (isCredentialsError) "Incorrect email or password" else null,
                imeAction = if (authMode == AuthMode.REGISTER) ImeAction.Next else ImeAction.Done
            )


            AnimatedContent(
                targetState = authMode,
                transitionSpec = {
                    val fade = fadeIn(tween(durationMillis = 150, delayMillis = 100)) togetherWith
                            fadeOut(tween(durationMillis = 100))
                    fade.using(SizeTransform(clip = false) { _, _ -> tween(durationMillis = 250) })
                },
                label = "authModeFields"
            ) { targetMode ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    when (targetMode) {
                        AuthMode.REGISTER -> PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm password",
                            enabled = !isLoading,
                            isVisible = isPasswordVisible,
                            onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                            errorMessage = confirmPasswordError,
                            imeAction = ImeAction.Done
                        )

                        AuthMode.LOGIN -> Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.End)
                                .noRippleClickable(onClick = onForgotPassword)
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                onClick = {
                    hasSubmitted = true
                    val trimmedEmail = email.trim()
                    val isValid = trimmedEmail.isNotBlank() &&
                            android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() &&
                            password.isNotBlank() &&
                            (authMode == AuthMode.LOGIN || password.length >= MIN_PASSWORD_LENGTH) &&
                            (authMode == AuthMode.LOGIN || confirmPassword == password)
                    if (isValid) onSubmit(authMode, trimmedEmail, password)
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    Text(
                        text = authMode.submitLabel,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LabelledDivider(label = "or continue with")

            Spacer(modifier = Modifier.height(8.dp))

            SocialProvider.entries.forEach { provider ->
                SocialSignInButton(
                    provider = provider,
                    enabled = !isLoading,
                    onClick = { onSocialSignIn(provider) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = authMode.footerPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        onAuthModeChange(authMode.toggled())
                        hasSubmitted = false
                    }
                ) {
                    Text(
                        text = authMode.footerAction,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    errorMessage: String?,
    imeAction: ImeAction,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        enabled = enabled,
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(text = it) } },
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        trailingIcon = {
            IconButton(onClick = onVisibilityToggle) {
                Icon(
                    painter = painterResource(
                        if (isVisible) R.drawable.ic_not_hidden else R.drawable.ic_hidden
                    ),
                    contentDescription = if (isVisible) "Hide password" else "Show password",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@Composable
private fun LabelledDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun SocialSignInButton(
    provider: SocialProvider,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val labelStyle = MaterialTheme.typography.labelLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    val labelWidth = remember(textMeasurer, labelStyle, density) {
        val widestPx = SocialProvider.entries.maxOf { entry ->
            textMeasurer.measure(entry.buttonLabel, labelStyle).size.width
        }
        with(density) { widestPx.toDp() }
    }

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(provider.iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (provider.tintWithTheme) MaterialTheme.colorScheme.onSurface
                else Color.Unspecified
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = provider.buttonLabel,
                style = labelStyle,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier.width(labelWidth)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthSheetLoginPreview() {
    MemoriesTheme {
        AuthSheet(
            authMode = AuthMode.LOGIN,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthSheetCredentialsErrorPreview() {
    MemoriesTheme {
        AuthSheet(
            authMode = AuthMode.LOGIN,
            isCredentialsError = true,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthSheetRegisterPreview() {
    MemoriesTheme {
        AuthSheet(
            authMode = AuthMode.REGISTER,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        )
    }
}
