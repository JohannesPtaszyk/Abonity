package dev.pott.abonity.common.text

abstract class TextFieldFilter(val block: (String) -> Unit) : (String) -> Unit {
    abstract fun isValid(newValue: String): Boolean

    override fun invoke(newValue: String) {
        if (newValue.isNotEmpty() && !isValid(newValue)) return
        block(newValue)
    }
}
