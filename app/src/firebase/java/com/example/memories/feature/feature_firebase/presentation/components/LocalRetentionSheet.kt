package com.example.memories.feature.feature_firebase.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.LocalRetention
import com.example.memories.core.domain.model.RetentionUnit
import com.example.memories.ui.theme.MemoriesTheme

private const val MAX_CUSTOM_AMOUNT = 999

private enum class RetentionOption { FOREVER, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, CUSTOM, NEVER }

private fun LocalRetention.toOption(): RetentionOption = when (this) {
    LocalRetention.Forever -> RetentionOption.FOREVER
    LocalRetention.Never -> RetentionOption.NEVER
    LocalRetention.OneMonth -> RetentionOption.ONE_MONTH
    LocalRetention.ThreeMonths -> RetentionOption.THREE_MONTHS
    LocalRetention.SixMonths -> RetentionOption.SIX_MONTHS
    is LocalRetention.Keep -> RetentionOption.CUSTOM
}

/** Short form for the settings row, e.g. "Forever", "3 months", "45 days". */
fun LocalRetention.label(): String = when (this) {
    LocalRetention.Forever -> "Forever"
    LocalRetention.Never -> "Never (removed once synced)"
    is LocalRetention.Keep -> periodLabel(amount, unit)
}

private fun periodLabel(amount: Int, unit: RetentionUnit): String {
    val word = when (unit) {
        RetentionUnit.DAYS -> "day"
        RetentionUnit.WEEKS -> "week"
        RetentionUnit.MONTHS -> "month"
    }
    return "$amount $word${if (amount == 1) "" else "s"}"
}

/**
 * Lets the user choose how long memories stay on this device. Presets save as they are; Custom
 * needs a valid amount (1–999) before Save is enabled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalRetentionSheet(
    current: LocalRetention,
    onSave: (LocalRetention) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    var selected by rememberSaveable { mutableStateOf(current.toOption()) }
    val currentCustom = (current as? LocalRetention.Keep)?.takeIf { current.toOption() == RetentionOption.CUSTOM }
    var customAmount by rememberSaveable { mutableStateOf(currentCustom?.amount?.toString() ?: "") }
    var customUnit by rememberSaveable { mutableStateOf(currentCustom?.unit ?: RetentionUnit.DAYS) }

    val customValue = customAmount.toIntOrNull()?.takeIf { it in 1..MAX_CUSTOM_AMOUNT }
    val result: LocalRetention? = when (selected) {
        RetentionOption.FOREVER -> LocalRetention.Forever
        RetentionOption.ONE_MONTH -> LocalRetention.OneMonth
        RetentionOption.THREE_MONTHS -> LocalRetention.ThreeMonths
        RetentionOption.SIX_MONTHS -> LocalRetention.SixMonths
        RetentionOption.CUSTOM -> customValue?.let { LocalRetention.Keep(it, customUnit) }
        RetentionOption.NEVER -> LocalRetention.Never
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Keep memories on this device",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Only memories that are safely synced to your account are ever removed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            RetentionOptionCard(
                title = "Forever",
                description = "Never remove memories from this device",
                selected = selected == RetentionOption.FOREVER,
                onSelect = { selected = RetentionOption.FOREVER },
            )
            listOf(
                RetentionOption.ONE_MONTH to LocalRetention.OneMonth,
                RetentionOption.THREE_MONTHS to LocalRetention.ThreeMonths,
                RetentionOption.SIX_MONTHS to LocalRetention.SixMonths,
            ).forEach { (option, preset) ->
                RetentionOptionCard(
                    title = "Keep ${preset.label()}",
                    description = "Remove synced memories older than ${preset.label()}",
                    selected = selected == option,
                    onSelect = { selected = option },
                )
            }
            RetentionOptionCard(
                title = "Custom",
                description = customValue?.let { "Keep ${periodLabel(it, customUnit)}" } ?: "Choose your own period",
                selected = selected == RetentionOption.CUSTOM,
                onSelect = { selected = RetentionOption.CUSTOM },
            ) {
                AnimatedVisibility(visible = selected == RetentionOption.CUSTOM) {
                    CustomPeriodInput(
                        amount = customAmount,
                        onAmountChange = { input ->
                            customAmount = input.filter(Char::isDigit).take(3)
                        },
                        isAmountError = customAmount.isNotEmpty() && customValue == null,
                        unit = customUnit,
                        onUnitChange = { customUnit = it },
                    )
                }
            }
            RetentionOptionCard(
                title = "Never",
                description = "Keep memories only until they're synced",
                selected = selected == RetentionOption.NEVER,
                onSelect = { selected = RetentionOption.NEVER },
            )

            Button(
                onClick = { result?.let(onSave) },
                enabled = result != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) {
                Text(text = "Save", modifier = Modifier.padding(vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun RetentionOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
    extraContent: @Composable () -> Unit = {},
) {
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                RadioButton(selected = selected, onClick = onSelect)
            }
            extraContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomPeriodInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isAmountError: Boolean,
    unit: RetentionUnit,
    onUnitChange: (RetentionUnit) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.width(88.dp),
            singleLine = true,
            isError = isAmountError,
            placeholder = { Text("30") },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.weight(1f)) {
            RetentionUnit.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = unit == option,
                    onClick = { onUnitChange(option) },
                    shape = SegmentedButtonDefaults.itemShape(index, RetentionUnit.entries.size),
                    label = {
                        Text(
                            when (option) {
                                RetentionUnit.DAYS -> "Days"
                                RetentionUnit.WEEKS -> "Weeks"
                                RetentionUnit.MONTHS -> "Months"
                            }
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LocalRetentionSheetPreview() {
    MemoriesTheme {
        LocalRetentionSheet(current = LocalRetention.ThreeMonths, onSave = {}, onDismiss = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LocalRetentionSheetCustomPreview() {
    MemoriesTheme {
        LocalRetentionSheet(
            current = LocalRetention.Keep(45, RetentionUnit.DAYS),
            onSave = {},
            onDismiss = {},
        )
    }
}
