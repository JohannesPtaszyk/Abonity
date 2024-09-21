package dev.pott.abonity.feature.subscription.overview

import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.serialization.Serializable

@Serializable
data class OverviewDestination(val detailId: SubscriptionId? = null)
