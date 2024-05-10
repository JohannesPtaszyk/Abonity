package dev.pott.abonity.common.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private const val PATTERN = "^([0-9]+(([,.])*[0-9]{0,2}))\$"

class PriceValueTextFieldFilter(block: (String) -> Unit) : TextFieldFilter(block) {

    private val filterRegex = PATTERN.toRegex()
    override fun isValid(newValue: String): Boolean {
        return filterRegex.matches(newValue)
    }
}

@Composable
fun rememberPriceValueFilter(block: (String) -> Unit) =
    remember(block) {
        PriceValueTextFieldFilter(block)
    }
