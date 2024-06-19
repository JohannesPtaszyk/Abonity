package dev.pott.abonity.common.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class DigitsOnlyTextFieldFilter(block: (String) -> Unit) : TextFieldFilter(block) {
    override fun isValid(newValue: String): Boolean = newValue.all { it.isDigit() }
}

@Composable
fun rememberDigitsFilter(block: (String) -> Unit) =
    remember(block) {
        DigitsOnlyTextFieldFilter(block)
    }
