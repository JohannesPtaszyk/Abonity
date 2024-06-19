package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import dev.pott.abonity.core.ui.R
import dev.pott.abonity.feature.subscription.add.ValidatedInput
import dev.pott.abonity.feature.subscription.add.localizedError

@Composable
fun NameInput(
    name: ValidatedInput,
    onNameChange: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        label = { Text(text = stringResource(id = R.string.subscription_add_label_name)) },
        value = name.value,
        onValueChange = onNameChange,
        isError = name.isError,
        supportingText = {
            name.localizedError()?.let { Text(text = it) }
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next,
        ),
    )
}
