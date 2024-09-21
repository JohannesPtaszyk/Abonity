package dev.pott.abonity.core.navigation

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.collections.immutable.persistentMapOf
import kotlin.reflect.typeOf

val coreNavTypeMap = persistentMapOf(
    typeOf<SubscriptionId?>() to SubscriptionIdNavType,
)
