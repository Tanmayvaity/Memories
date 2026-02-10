package com.example.memories.feature.feature_other.presentation.screens.hidden_memory_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.memories.R
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.presentation.components.CustomSettingRow
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.presentation.components.SettingCard
import com.example.memories.core.util.noRippleClickable
import com.example.memories.feature.feature_other.domain.model.LockDuration
import com.example.memories.feature.feature_other.domain.model.LockMethod
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingEvents
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingScreenState
import com.example.memories.feature.feature_other.presentation.viewmodels.HiddenMemorySettingViewModel
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun HiddenMemorySettingRoot(
    onBack: () -> Unit,
    viewModel: HiddenMemorySettingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HiddenMemorySettingScreen(
        onBack = onBack,
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenMemorySettingScreen(
    onBack: () -> Unit = {},
    state: HiddenMemorySettingScreenState = HiddenMemorySettingScreenState(),
    onEvent: (HiddenMemorySettingEvents) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppTopBar(
                showDivider = false,
                title = {
                    Text(
                        text = "Hidden Memories Settings",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                showNavigationIcon = true,
                onNavigationIconClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            HeaderSection()

            LockMethodSection(
                currentLockMethod = state.currentLockMethod,
                onSetLockMethod = { onEvent(HiddenMemorySettingEvents.SetLockMethod(it)) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            LockDurationSection(
                currentLockDuration = state.currentLockDuration,
                onSetLockDuration = { onEvent(HiddenMemorySettingEvents.SetLockDuration(it)) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DataManagementSection(
                onUnhideAll = { /* TODO */ },
                onDeleteAll = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconItem(
                drawableRes = R.drawable.ic_hidden,
                contentDescription = "Hidden Icon",
                modifier = Modifier
                    .size(128.dp),
                iconSize = 64.dp,
                color = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                alpha = 0.1f
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage how your hidden memories are secured and displayed within the app",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LockMethodSection(
    currentLockMethod: LockMethod,
    onSetLockMethod: (LockMethod) -> Unit
) {
    SectionTitle("Lock Method")
    Spacer(modifier = Modifier.height(16.dp))
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            LockMethod.entries.forEachIndexed { index, method ->
                CustomSettingRow(
                    modifier = Modifier.padding(8.dp),
                    heading = method.title,
                    content = method.description,
                    drawableRes = method.icon,
                    contentDescription = method.description,
                    showDivider = index != LockMethod.entries.lastIndex,
                    showCustomContent = index == LockMethod.CUSTOM_PIN.ordinal,
                    customContent = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilledTonalButton(
                                onClick = { onSetLockMethod(method) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_lock),
                                        contentDescription = "Lock icon"
                                    )
                                    Text(text = "Setup/Change PIN")
                                }
                            }
                        }
                    },
                    onClick = { onSetLockMethod(method) },
                    endContent = {
                        RadioButton(
                            selected = method == currentLockMethod,
                            onClick = { onSetLockMethod(method) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LockDurationSection(
    currentLockDuration: LockDuration,
    onSetLockDuration: (LockDuration) -> Unit
) {
    SectionTitle("Lock Duration")
    Spacer(modifier = Modifier.height(16.dp))
    SettingCard(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            LockDuration.entries.forEachIndexed { index, duration ->
                CustomSettingRow(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onSetLockDuration(duration) },
                    heading = duration.title,
                    showDivider = index != LockDuration.entries.lastIndex,
                    showContentAtEnd = false,
                    showCustomContent = false,
                    endContent = {
                        RadioButton(
                            selected = duration == currentLockDuration,
                            onClick = { onSetLockDuration(duration) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DataManagementSection(
    onUnhideAll: () -> Unit,
    onDeleteAll: () -> Unit
) {
    SectionTitle("Data Management")
    Spacer(modifier = Modifier.height(8.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = onUnhideAll,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Unhide All Hidden Memories")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDeleteAll,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon"
                )
                Text(text = "Delete All Hidden Memories")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Note: Deleting Hidden Memories is permanent",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
private fun HiddenMemorySettingScreenPreview() {
    MemoriesTheme {
        HiddenMemorySettingScreen()
    }
}