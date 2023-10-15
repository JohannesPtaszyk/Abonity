package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription

data class SubscriptionOverviewItem(
    val subscription: Subscription,
    val periodPrice: Price,
)
