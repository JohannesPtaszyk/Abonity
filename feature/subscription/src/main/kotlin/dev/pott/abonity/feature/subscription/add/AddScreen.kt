package dev.pott.abonity.feature.subscription.add

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.subscription.Category
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Clock
import java.util.Currency

@Composable
fun AddScreen(
    close: () -> Unit,
    promptAppStoreReview: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.formState.saving, close, promptAppStoreReview) {
        if (state.formState.saving == AddState.SavingState.SAVED) {
            close()
            promptAppStoreReview()
        }
    }
    AddScreen(
        state = state,
        onPaymentDateChange = viewModel::setPaymentDate,
        onNameChange = viewModel::setName,
        onDescriptionChange = viewModel::setDescription,
        onPriceChange = viewModel::setPrice,
        onCurrencyChange = viewModel::setCurrency,
        onIsPeriodicChange = viewModel::setPeriodic,
        onPeriodChange = viewModel::setPaymentPeriod,
        onPeriodCountChange = viewModel::setPaymentPeriodCount,
        resetSavingState = viewModel::resetSavingState,
        onSaveClick = viewModel::save,
        onCloseClick = close,
        onAddNewCategory = viewModel::addCategory,
        onCloseAddNewCategory = viewModel::closeAddCategoryDialog,
        onOpenAddNewCategory = viewModel::openAddCategoryDialog,
        onCategorySelect = viewModel::selectCategory,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onPaymentDateChange: (epochMilliseconds: Long) -> Unit,
    onNameChange: (name: String) -> Unit,
    onDescriptionChange: (name: String) -> Unit,
    onPriceChange: (priceValue: String) -> Unit,
    onCurrencyChange: (currency: Currency) -> Unit,
    onIsPeriodicChange: (isPeriodic: Boolean) -> Unit,
    onPeriodChange: (period: PaymentPeriod) -> Unit,
    onPeriodCountChange: (countValue: String) -> Unit,
    resetSavingState: () -> Unit,
    onSaveClick: () -> Unit,
    onCloseClick: () -> Unit,
    onAddNewCategory: (name: String) -> Unit,
    onCloseAddNewCategory: () -> Unit,
    onOpenAddNewCategory: () -> Unit,
    modifier: Modifier = Modifier,
    onCategorySelect: (category: Category) -> Unit,
) {
    if (state.formState.saving == AddState.SavingState.ERROR) {
        SavingErrorDialog(resetSavingState)
    }

    if (state.formState.showCategoryDialog) {
        AddNewCategoryDialog(onCloseAddNewCategory, onAddNewCategory)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = if (state.showNameAsTitle) {
                        state.formState.name.value
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
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .imePadding(),
            ) {
                item {
                    CategoryInput(
                        categories = state.categories,
                        selectedCategories = state.formState.selectedCategories,
                        onCategorySelecte = onCategorySelect,
                        onOpenAddNewCategory = onOpenAddNewCategory,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    NameInput(
                        name = state.formState.name,
                        onNameChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    PriceInput(
                        priceValue = state.formState.priceValue,
                        currency = state.formState.currency,
                        onPriceChange = onPriceChange,
                        onCurrencyChange = onCurrencyChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    DateInput(
                        isPeriodic = !state.formState.isOneTimePayment,
                        paymentDateEpochMillis = state.formState.paymentDateEpochMillis,
                        periodCount = state.formState.paymentPeriodCount,
                        period = state.formState.paymentPeriod,
                        onIsPeriodicChange = onIsPeriodicChange,
                        onPeriodCountChange = onPeriodCountChange,
                        onPeriodChange = onPeriodChange,
                        onPaymentDateChange = onPaymentDateChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    DescriptionInput(
                        description = state.formState.description,
                        onDescriptionChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryInput(
    categories: ImmutableList<Category>,
    selectedCategories: ImmutableList<Category>,
    onCategorySelecte: (category: Category) -> Unit,
    onOpenAddNewCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "add_new_category") {
            AssistChip(
                onClick = onOpenAddNewCategory,
                label = { Text(text = stringResource(id = R.string.add_new_category_cta)) },
                leadingIcon = {
                    Icon(
                        painter = rememberVectorPainter(image = AppIcons.Add),
                        contentDescription = null,
                    )
                },
                modifier = Modifier.animateItem(),
            )
        }
        items(categories, key = { it.id.value }) {
            InputChip(
                selected = selectedCategories.contains(it),
                onClick = { onCategorySelecte(it) },
                label = { Text(text = it.name) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun AddNewCategoryDialog(
    onCloseAddNewCategory: () -> Unit,
    onAddNewCategory: (name: String) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.category_add_dialog_title))
        },
        text = {
            OutlinedTextField(
                placeholder = {
                    Text(text = stringResource(id = R.string.category_add_dialog_input_placeholder))
                },
                value = input,
                onValueChange = { input = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onAddNewCategory(input) }),
            )
        },
        onDismissRequest = onCloseAddNewCategory,
        confirmButton = {
            TextButton(onClick = { onAddNewCategory(input) }) {
                Text(text = stringResource(id = R.string.category_add_dialog_cta_add))
            }
        },
        dismissButton = {
            TextButton(onClick = { onCloseAddNewCategory() }) {
                Text(text = stringResource(id = R.string.dialog_btn_dismiss_default))
            }
        },
    )
}

@Composable
private fun SavingErrorDialog(resetSavingState: () -> Unit) {
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

@Composable
@PreviewCommonScreenConfig
private fun AddScreenPreview() {
    AddScreen(
        state = AddState(
            formState = AddFormState(Clock.System.now().toEpochMilliseconds()),
        ),
        onPaymentDateChange = {},
        onNameChange = {},
        onDescriptionChange = {},
        onPriceChange = {},
        onCurrencyChange = {},
        onIsPeriodicChange = {},
        onPeriodChange = {},
        onPeriodCountChange = {},
        resetSavingState = {},
        onSaveClick = {},
        onCloseClick = {},
        onAddNewCategory = {},
        onCloseAddNewCategory = {},
        onOpenAddNewCategory = {},
        onCategorySelect = {},
    )
}
