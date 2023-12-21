package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription

    fun getSubscriptionsFlow(): Flow<List<Subscription>>

    fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription>
}
