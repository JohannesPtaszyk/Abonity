package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ListItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.pott.abonity.common.text.DigitsOnlyTextFieldFilter
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.subscription.rememberFormattedDate
import dev.pott.abonity.core.ui.string.paymentPeriodPluralRes

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DateInput(
    isPeriodic: Boolean,
    paymentDateEpochMillis: Long?,
    periodCount: Int?,
    period: PaymentPeriod?,
    onIsPeriodicChanged: (isPeriodic: Boolean) -> Unit,
    onPeriodCountChanged: (countValue: String) -> Unit,
    onPeriodChanged: (period: PaymentPeriod) -> Unit,
    onPaymentDateChanged: (epochMilliseconds: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val date = paymentDateEpochMillis?.let { rememberFormattedDate(it) }
    val interactionSource = remember { MutableInteractionSource() }
    val pressedState by interactionSource.interactions.collectAsState(
        initial = PressInteraction.Cancel(PressInteraction.Press(Offset.Zero)),
    )
    LaunchedEffect(pressedState) {
        if (pressedState is PressInteraction.Release) {
            showDatePicker = true
            interactionSource.emit(PressInteraction.Cancel(PressInteraction.Press(Offset.Zero)))
        }
    }

    Column(modifier = modifier) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SegmentedButton(
                selected = isPeriodic,
                onClick = { onIsPeriodicChanged(true) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(text = stringResource(id = R.string.subscription_add_btn_periodic))
            }
            SegmentedButton(
                selected = !isPeriodic,
                onClick = { onIsPeriodicChanged(false) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(text = stringResource(id = R.string.subscription_add_btn_one_time))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedContent(
            isPeriodic,
            label = "payment_type_form_transition",
        ) { isPeriodicPayment ->
            if (isPeriodicPayment) {
                PeriodicInput(
                    date,
                    interactionSource,
                    periodCount,
                    onPeriodCountChanged,
                    period,
                    onPeriodChanged,
                )
            } else {
                TextField(
                    readOnly = true,
                    value = date.orEmpty(),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.subscription_add_label_date)) },
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(onPaymentDateChanged)
                        showDatePicker = false
                    },
                ) {
                    Text(text = stringResource(id = R.string.dialog_btn_confirm_default))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = R.string.dialog_btn_dismiss_default))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PeriodicInput(
    date: String?,
    interactionSource: MutableInteractionSource,
    periodCount: Int?,
    onPeriodCountChanged: (countValue: String) -> Unit,
    period: PaymentPeriod?,
    onPeriodChanged: (period: PaymentPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        TextField(
            readOnly = true,
            value = date.orEmpty(),
            onValueChange = {},
            label = {
                Text(text = stringResource(id = R.string.subscription_add_label_first_payment_date))
            },
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            TextField(
                value = periodCount?.toString().orEmpty(),
                onValueChange = DigitsOnlyTextFieldFilter(onPeriodCountChanged),
                label = {
                    Text(text = stringResource(id = R.string.subscription_add_label_period_count))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(weight = 0.6f),
            )
            Spacer(Modifier.width(8.dp))
            PeriodDropDown(period, periodCount, onPeriodChanged, Modifier.weight(1f))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PeriodDropDown(
    period: PaymentPeriod?,
    currentPeriodCount: Int?,
    onPeriodChanged: (period: PaymentPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPeriodDropdown by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = showPeriodDropdown,
        onExpandedChange = { showPeriodDropdown = it },
        modifier = modifier,
    ) {
        TextField(
            readOnly = true,
            value = period?.let {
                val textRes = paymentPeriodPluralRes(it = it)
                pluralStringResource(id = textRes, currentPeriodCount ?: 0)
            }.orEmpty(),
            onValueChange = {},
            label = { Text(text = stringResource(id = R.string.subscription_add_label_period)) },
            modifier = Modifier
                .menuAnchor()
                .clickable(
                    role = Role.Button,
                    onClickLabel = stringResource(
                        id = R.string.subscription_add_label_period_click,
                    ),
                ) {
                    showPeriodDropdown = !showPeriodDropdown
                },
        )
        ExposedDropdownMenu(
            expanded = showPeriodDropdown,
            onDismissRequest = { showPeriodDropdown = false },
        ) {
            PaymentPeriod.entries.forEach {
                ListItem(
                    headlineContent = {
                        val textRes = paymentPeriodPluralRes(it)
                        Text(text = pluralStringResource(id = textRes, currentPeriodCount ?: 0))
                    },
                    modifier = Modifier.clickable(
                        role = Role.Button,

                    ) {
                        onPeriodChanged(it)
                    },
                )
            }
        }
    }
}
