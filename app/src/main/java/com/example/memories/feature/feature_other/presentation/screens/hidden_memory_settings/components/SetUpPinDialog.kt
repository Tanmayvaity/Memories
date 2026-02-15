package com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class PinType {
    SETUP,
    CONFIRM
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPinDialog(
    modifier: Modifier = Modifier,
    onDismiss: (Boolean) -> Unit = {},
    onPinChange: (String) -> Unit = {}
) {
    var pin by rememberSaveable { mutableStateOf("") }
    var confirmPin by rememberSaveable { mutableStateOf("") }
    var pinType by rememberSaveable { mutableStateOf(PinType.SETUP) }
    val scope = rememberCoroutineScope()


    var isError by remember { mutableStateOf(false) }

    // Shake animation for error
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            // Quick horizontal shake
            repeat(3) {
                shakeOffset.animateTo(10f, tween(40))
                shakeOffset.animateTo(-10f, tween(40))
            }
            shakeOffset.animateTo(0f, tween(40))
            isError = false
        }
    }


    Dialog(
        onDismissRequest = {
            onDismiss(false)
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when(pinType){
                                PinType.SETUP -> "Create a PIN"
                                PinType.CONFIRM -> "Confirm Your PIN"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onDismiss(false)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AnimatedContent(
                    targetState = pinType,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                    },
                    label = "pinTypeText"
                ) { type ->
                    Text(
                        text = when (type) {
                            PinType.SETUP -> "This PIN will be used to protect your hidden memories"
                            PinType.CONFIRM -> "Please re-enter your PIN to confirm"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                PinDotRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .offset(x = shakeOffset.value.dp)
                    ,
                    pin = if(pinType == PinType.SETUP) pin else confirmPin
                )
                PinPad(
                    modifier = Modifier,
                    onDigitClick = { digit ->
                        val currentPin = if (pinType == PinType.SETUP) pin else confirmPin

                        // Single guard clause
                        if (currentPin.length >= 4) return@PinPad
                        scope.launch {
                            if (pinType == PinType.SETUP) {
                                pin += digit.toString()
                                if (pin.length == 4) {
                                    delay(100)
                                    pinType = PinType.CONFIRM
                                }
                            } else {
                                confirmPin += digit.toString()
                                if (confirmPin.length == 4) {

                                    delay(300)
                                    if (pin == confirmPin) {
                                        onPinChange(pin)
                                        onDismiss(true)
                                    } else {
                                        isError = true
                                        confirmPin = ""
                                    }


                                }
                            }
                        }

                    },
                    onBackSpaceClick = {
                        when(pinType){
                            PinType.SETUP -> {
                                pin = pin.dropLast(1)
                            }
                            PinType.CONFIRM -> {
                                confirmPin = confirmPin.dropLast(1)
                            }
                        }
                    }

                )
            }


        }
    }

}

@Preview
@Composable
private fun SetupPinDialogPreview() {
    MemoriesTheme {
        SetupPinDialog(
            onDismiss = {}
        )
    }
}