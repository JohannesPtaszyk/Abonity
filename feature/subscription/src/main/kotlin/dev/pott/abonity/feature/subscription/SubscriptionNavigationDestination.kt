package dev.pott.abonity.feature.subscription

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionNavigationDestination(val detailId: SubscriptionId? = null)
