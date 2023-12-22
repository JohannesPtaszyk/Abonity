package dev.pott.abonity.core.test.subscription

import dev.pott.abonity.core.domain.subscription.SubscriptionRepository
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeSubscriptionRepository(
    private val subscriptionsFlow: Flow<List<Subscription>> = emptyFlow(),
    private val subscriptionFlow: Flow<Subscription> = emptyFlow(),
) : SubscriptionRepository {

    val addedSubscriptions = mutableListOf<Subscription>()

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription {
        addedSubscriptions.add(subscription)
        return subscription
    }
    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return subscriptionsFlow
    }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription> {
        return subscriptionFlow
    }
}
