package dev.pott.abonity.core.entity.subscription

@JvmInline
value class SubscriptionId(val value: Long) {
    companion object {
        fun none() = SubscriptionId(0)
    }
}
