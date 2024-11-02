package dev.pott.abonity.core.entity.subscription

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class SubscriptionId(val value: Long) {
    companion object {
        fun none() = SubscriptionId(0)
        fun parse(id: Long?): SubscriptionId? = id?.takeIf { it != -1L }?.let { SubscriptionId(it) }
    }
}
