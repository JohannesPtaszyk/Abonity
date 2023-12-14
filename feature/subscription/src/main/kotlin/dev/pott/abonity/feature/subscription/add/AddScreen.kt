package dev.pott.abonity.feature.subscription.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.navigation.CloseButton
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig
import dev.pott.abonity.core.ui.theme.AppIcons
import dev.pott.abonity.core.ui.util.plus
import dev.pott.abonity.core.ui.util.rememberDefaultLocale
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.Currency

@Composable
fun AddScreen(
    close: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.savingState) {
        if (state.savingState == AddState.SavingState.SAVED) {
            close()
        }
    }
    AddScreen(
        state = state,
        onInputChanged = viewModel::updateInputs,
        save = viewModel::save,
        close = close,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    state: AddState,
    onInputChanged: (AddFormInput) -> Unit,
    save: () -> Unit,
    close: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = stringResource(id = R.string.add_subscription_title)
                    Text(title)
                },
                navigationIcon = {
                    CloseButton(onClick = close)
                },
                actions = {
                    Button(onClick = save) {
                        Icon(
                            painter = rememberVectorPainter(image = AppIcons.Save),
                            // TODO add content description
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Save")
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
            ) {
                item {
                    TextField(
                        label = { Text(text = "Name") },
                        value = state.input.name,
                        onValueChange = { onInputChanged(state.input.copy(name = it)) },
                    )
                }
                item {
                    TextField(
                        label = { Text(text = "Price") },
                        value = state.input.description,
                        onValueChange = { onInputChanged(state.input.copy(description = it)) },
                    )
                }
                item {
                    val options = remember { Currency.getAvailableCurrencies().toImmutableList() }
                    var expanded by remember { mutableStateOf(false) }
                    var selectedOptionText by remember { mutableStateOf(options[0]) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        },
                    ) {
                        val locale = rememberDefaultLocale()
                        TextField(
                            readOnly = true,
                            value = selectedOptionText.getDisplayName(locale),
                            onValueChange = {
                                // As this is read-only, we do not need to do anyhthing
                            },
                            label = { Text("Currency") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded,
                                )
                            },
                            modifier = Modifier.clickable { expanded != expanded },
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            },
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                    },
                                    text = {
                                        Text(text = selectionOption.getDisplayName(locale))
                                    },
                                    trailingIcon = {
                                        Text(text = selectionOption.getSymbol(locale))
                                    },
                                )
                            }
                        }
                    }
                }
                item {
                    TextField(
                        label = { Text(text = "Description") },
                        value = state.input.description,
                        onValueChange = { onInputChanged(state.input.copy(description = it)) },
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
            AddFormInput(Clock.System.todayIn(TimeZone.currentSystemDefault())),
            AddState.SavingState.IDLE,
            false,
        ),
        onInputChanged = {},
        save = {},
        close = {},
    )
}
