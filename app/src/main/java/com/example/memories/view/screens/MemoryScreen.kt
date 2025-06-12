package com.example.memories.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    uri: String,
    onArrowBackButtonClick: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val columnScrollState = rememberScrollState()

    var showProgressBar by remember { mutableStateOf(false) }


    LaunchedEffect(showProgressBar) {
        delay(3000)
        showProgressBar = false
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Memory",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onArrowBackButtonClick()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to the previous screen"
                        )
                    }
                },
                actions = {
                    if (!showProgressBar) {

                    }
                    Button(
                        onClick = {
                            showProgressBar = true
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black
                        ),
                        enabled = !showProgressBar
                    ) {
                        if(!showProgressBar){
                            Text(
                                text = "Create",
                            )
                        }

                        if(showProgressBar){
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Color.Gray,
                            )
                        }

                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    scrolledContainerColor = Color.White
                ),
                scrollBehavior = scrollBehavior
            )
        }

    ) { innerPadding ->

        var captionText by remember { mutableStateOf("") }
        var descriptionText by remember { mutableStateOf("") }

        val datePickerState = rememberDatePickerState()

        var showDatePicker by remember { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color.White)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .verticalScroll(columnScrollState)
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Chosen image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(top = 5.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Caption",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = captionText,
                    onValueChange = { it ->
                        captionText = it
                    },
                    label = {
                        Text("Write a caption")
                    },
                    modifier = Modifier
                        .padding(start = 15.dp, top = 5.dp, bottom = 5.dp, end = 15.dp)
                        .height(75.dp)
                        .fillMaxWidth(),
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.None,
                        keyboardType = KeyboardType.Text
                    ),


                    )

                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
                    fontWeight = FontWeight.Bold

                )

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { it ->
                        descriptionText = it
                    },
                    label = {
                        Text("Write a description")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(start = 15.dp, top = 5.dp, bottom = 5.dp, end = 15.dp),

                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.None,
                        keyboardType = KeyboardType.Text
                    )
                )

                Text(
                    text = "Reminder",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp, bottom = 5.dp),
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, top = 10.dp, bottom = 5.dp, end = 15.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Set a reminder"
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                }


            }
        }

        if(showDatePicker){
            ReminderDatePickerDialog(
                onDismiss = {
                    showDatePicker = false
                },
                onConfirm = {
                    showDatePicker = false
                },
                state = datePickerState
            )
        }


    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePickerDialog(
    modifier: Modifier = Modifier,
    onDismiss : () -> Unit = {},
    onConfirm : () -> Unit = {},
    state: DatePickerState = rememberDatePickerState()
) {
    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                   onConfirm()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }


    ) {
        DatePicker(state = state )
    }
}