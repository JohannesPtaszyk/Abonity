package dev.pott.abonity.common.text

class DigitsOnlyTextFieldFilter(block: (String) -> Unit) : TextFieldFilter(block) {
    override fun isValid(newValue: String): Boolean {
        return newValue.all { it.isDigit() }
    }
}
