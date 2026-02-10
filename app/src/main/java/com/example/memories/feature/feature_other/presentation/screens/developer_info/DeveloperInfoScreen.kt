package com.example.memories.feature.feature_other.presentation.screens.developer_info

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.R
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_other.domain.model.GithubUserInfo
import com.example.memories.feature.feature_other.presentation.viewmodels.DeveloperInfoState
import com.example.memories.feature.feature_other.presentation.viewmodels.DeveloperScreenEvent
import com.example.memories.feature.feature_other.presentation.viewmodels.DeveloperScreenViewModel
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun DeveloperInfoRoot(
    modifier: Modifier = Modifier,
    viewModel: DeveloperScreenViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var errorText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit){
        viewModel.events.collect { event ->
            when(event){
                is DeveloperScreenEvent.Error -> {
                    errorText = event.message
                }

            }
        }
    }

    DeveloperInfoScreen(
        state = state,
        onBack = onBack,
        onRefresh = {
            viewModel.fetchUser()
        }
    )
    if(state.error != null){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperInfoScreen(
    modifier: Modifier = Modifier,
    state: DeveloperInfoState = DeveloperInfoState(),
    onRefresh: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isPreviewMode = LocalInspectionMode.current
    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                title = {
                    Text(
                        text = "Developer Info"
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack,
                showAction = true,
                actionContent = {
                    IconItem(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Icon",
                        alpha = 0f,
                        onClick = onRefresh,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        },
        modifier = Modifier

    ) { innerPadding ->
        if (state.isLoading && state.error == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator(
                    text = "Fetching Developer Info",
                    showText = true,
                )
            }
        }

        if (state.user != null && state.error == null) {
            val user = state.user
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            if(isPreviewMode) R.drawable.ic_launcher_background
                            else user.avatarUrl
                        )
                        .crossfade(true)
                        .listener(
                            onError = { _, result ->
                                Log.e("DeveloperInfoScreen", "Error: ${result.throwable.message}")
                            },
                            onSuccess = { _, _ ->
                                Log.d("DeveloperInfoScreen", "Image loaded successfully")
                            }
                        )
                        .build(),
                    loading = {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                )
                user.location?.let { location ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = "Location Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = location,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "${user?.name}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${user?.bio}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.1f),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Row() {
                        IconItem(
                            drawableRes = R.drawable.ic_github,
                            contentDescription = "Github Icon",
                            alpha = 0f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = {
                                user.profileUrl?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                    context.startActivity(intent)
                                }
//                                val developerUri = "https://github.com/Tanmayvaity"

                            }
                        )
                        IconItem(
                            drawableRes = R.drawable.ic_linkedin,
                            contentDescription = "Github Icon",
                            alpha = 0f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = {
                                val linkedinUrl = "https://www.linkedin.com/in/tanmay-vaity"
                                val intent = Intent(Intent.ACTION_VIEW, linkedinUrl.toUri())
                                context.startActivity(intent)
                            }
                        )

                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

            }
        }


    }

}


@Preview
@Composable
private fun DeveloperInfoScreenPreview() {
    MemoriesTheme {
        DeveloperInfoScreen(
            state = DeveloperInfoState(
                user = GithubUserInfo(
                    name = "Tanmay",
                    bio = "Android Developer",
                    profileUrl = "",
                    avatarUrl = "",
                    id = 1,
                    location = "India",
                    createAt = "Oct 2023"
                )

            )
        )
    }
}