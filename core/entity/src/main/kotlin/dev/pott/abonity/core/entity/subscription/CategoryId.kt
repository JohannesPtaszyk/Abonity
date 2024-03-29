package dev.pott.abonity.core.entity.subscription

@JvmInline
value class CategoryId(val value: Long) {
    companion object {
        fun none() = CategoryId(0)
    }
}
