package dev.pott.abonity.feature.subscription.overview

import androidx.compose.runtime.Stable
import dev.pott.abonity.core.entity.Price
import dev.pott.abonity.core.entity.Subscription

data class SubscriptionItem(
    @Stable val subscription: Subscription,
    @Stable val periodPrice: Price,
)
