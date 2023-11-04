package dev.pott.abonity.core.entity

@JvmInline
value class SubscriptionId(val id: Long) {
    companion object {
        fun none() = SubscriptionId(0)
    }
}
