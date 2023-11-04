package dev.pott.abonity.feature.subscription.add

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.core.ui.components.CloseButton
import dev.pott.abonity.core.ui.preview.PreviewCommonScreenConfig

@Composable
fun AddScreen(modifier: Modifier = Modifier, viewModel: AddScreenViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddScreen(state = state, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(state: AddState, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = state.input?.name
                        ?: stringResource(id = R.string.add_subscription_title)
                    Text(title)
                },
                navigationIcon = {
                    CloseButton(onClick = { /*TODO*/ })
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
        }
    }
}

@Composable
@PreviewCommonScreenConfig
private fun AddScreenPreview() {
    AddScreen(state = AddState(null, false))
}
