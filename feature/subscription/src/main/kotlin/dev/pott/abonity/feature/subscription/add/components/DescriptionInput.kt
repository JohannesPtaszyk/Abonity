package dev.pott.abonity.feature.subscription.add.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import dev.pott.abonity.core.ui.R

@Composable
fun DescriptionInput(
    description: String?,
    onDescriptionChanged: (name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        label = { Text(text = stringResource(id = R.string.subscription_add_label_description)) },
        value = description.orEmpty(),
        onValueChange = onDescriptionChanged,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        modifier = modifier.heightIn(min = 200.dp),
    )
}
