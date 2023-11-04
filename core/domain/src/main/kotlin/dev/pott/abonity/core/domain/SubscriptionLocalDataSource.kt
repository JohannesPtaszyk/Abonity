package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow

interface SubscriptionLocalDataSource {
    suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription
    fun getSubscriptionsFlow(): Flow<List<Subscription>>
    fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription>
}
