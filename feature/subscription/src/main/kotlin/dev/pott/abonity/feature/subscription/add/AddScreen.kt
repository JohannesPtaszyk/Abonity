package dev.pott.abonity.feature.subscription.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.subscription.PaymentPeriod
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.navigation.CloseButton
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.feature.subscription.add.components.DateInput
import dev.pott.abonity.feature.subscription.add.components.DescriptionInput
import dev.pott.abonity.feature.subscription.add.components.NameInput
import dev.pott.abonity.feature.subscription.add.components.PriceInput
import kotlinx.datetime.Clock
import java.util.Currency

@Composable
fun AddScreen(
    close: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.savingState) {
        if (state.savingState == AddState.SavingState.SAVED) {
            close()
        }
    }
    AddScreen(
        state = state,
        onPaymentDateChanged = viewModel::setPaymentDate,
        onNameChanged = viewModel::setName,
        onDescriptionChanged = viewModel::setDescription,
        onPriceChanged = viewModel::setPrice,
        onCurrencyChanged = viewModel::setCurrency,
        onIsPeriodicChanged = viewModel::setPeriodic,
        onPeriodChanged = viewModel::setPaymentPeriod,
        onPeriodCountChanged = viewModel::setPaymentPeriodCount,
        resetSavingState = viewModel::resetSavingState,
        onSaveClick = viewModel::save,
        onCloseClick = close,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onPaymentDateChanged: (epochMilliseconds: Long) -> Unit,
    onNameChanged: (name: String) -> Unit,
    onDescriptionChanged: (name: String) -> Unit,
    onPriceChanged: (priceValue: String) -> Unit,
    onCurrencyChanged: (currency: Currency) -> Unit,
    onIsPeriodicChanged: (isPeriodic: Boolean) -> Unit,
    onPeriodChanged: (period: PaymentPeriod) -> Unit,
    onPeriodCountChanged: (countValue: String) -> Unit,
    resetSavingState: () -> Unit,
    onSaveClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.savingState == AddState.SavingState.ERROR) {
        AlertDialog(
            onDismissRequest = resetSavingState,
            title = {
                Text(
                    text = stringResource(
                        id = R.string.add_validation_saving_error_invalid_fields_title,
                    ),
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.add_validation_saving_error_invalid_fields_description,
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = resetSavingState) {
                    Text(
                        text = stringResource(
                            id = R.string.add_validation_saving_error_invalid_fields_btn,
                        ),
                    )
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = if (state.showNameAsTitle) {
                        state.input.name.value
                    } else {
                        stringResource(id = R.string.add_subscription_title)
                    }
                    Text(title)
                },
                navigationIcon = {
                    CloseButton(onClick = onCloseClick)
                },
                actions = {
                    Button(onClick = onSaveClick) {
                        Icon(
                            painter = rememberVectorPainter(image = AppIcons.Save),
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.add_subscription_btn))
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
                contentPadding = paddingValues + PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 600.dp),
            ) {
                item {
                    NameInput(
                        name = state.input.name,
                        onNameChanged = onNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    PriceInput(
                        priceValue = state.input.priceValue,
                        currency = state.input.currency,
                        onPriceChanged = onPriceChanged,
                        onCurrencyChanged = onCurrencyChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item { HorizontalDivider() }
                item {
                    DateInput(
                        isPeriodic = !state.input.isOneTimePayment,
                        paymentDateEpochMillis = state.input.paymentDateEpochMillis,
                        periodCount = state.input.paymentPeriodCount,
                        period = state.input.paymentPeriod,
                        onIsPeriodicChanged = onIsPeriodicChanged,
                        onPeriodCountChanged = onPeriodCountChanged,
                        onPeriodChanged = onPeriodChanged,
                        onPaymentDateChanged = onPaymentDateChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item { HorizontalDivider() }
                item {
                    DescriptionInput(
                        description = state.input.description,
                        onDescriptionChanged = onDescriptionChanged,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
@PreviewCommonScreenConfig
private fun AddScreenPreview() {
    AddScreen(
        state = AddState(
            input = AddFormState(Clock.System.now().toEpochMilliseconds()),
            savingState = AddState.SavingState.IDLE,
            loading = false,
        ),
        onPaymentDateChanged = {},
        onNameChanged = {},
        onDescriptionChanged = {},
        onPriceChanged = {},
        onCurrencyChanged = {},
        onIsPeriodicChanged = {},
        onPeriodChanged = {},
        onPeriodCountChanged = {},
        resetSavingState = {},
        onSaveClick = {},
        onCloseClick = {},
    )
}
