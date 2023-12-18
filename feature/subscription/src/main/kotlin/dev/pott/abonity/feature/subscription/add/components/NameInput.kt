package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.feature.subscription.add.AddState

@Composable
fun NameInput(
    state: AddState,
    onNameChanged: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        label = { Text(text = stringResource(id = R.string.subscription_add_label_name)) },
        value = state.input.name,
        onValueChange = onNameChanged,
        modifier = modifier,
    )
}
