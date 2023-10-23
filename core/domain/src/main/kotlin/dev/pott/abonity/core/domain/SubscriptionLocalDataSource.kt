package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow

interface SubscriptionLocalDataSource {
    fun getSubscriptionsFlow(): Flow<List<Subscription>>

    fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription>
}
