package dev.pott.abonity.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import dev.pott.abonity.core.entity.subscription.SubscriptionId

private const val DEFAULT_VALUE = -1L

object SubscriptionIdNavType : NavType<SubscriptionId?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): SubscriptionId? =
        bundle.getLong(key, DEFAULT_VALUE).takeIf { it != DEFAULT_VALUE }?.let {
            SubscriptionId(it)
        }

    override fun parseValue(value: String): SubscriptionId = SubscriptionId(value.toLong())

    override fun put(bundle: Bundle, key: String, value: SubscriptionId?) {
        value?.let { bundle.putLong(key, it.value) }
    }
}
