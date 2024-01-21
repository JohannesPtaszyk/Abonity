package dev.pott.abonity.core.test.subscription

import dev.pott.abonity.core.domain.subscription.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeSubscriptionLocalDataSource(
    private val testSubscriptionsFlow: Flow<List<Subscription>> = emptyFlow(),
    private val testSubscriptionFlow: Flow<Subscription?> = emptyFlow(),
) : SubscriptionLocalDataSource {

    val addedSubscriptions = mutableListOf<Subscription>()
    val deletedSubscriptions = mutableListOf<SubscriptionId>()

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription {
        addedSubscriptions.add(subscription)
        return subscription
    }

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return testSubscriptionsFlow
    }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription?> {
        return testSubscriptionFlow
    }

    override suspend fun deleteSubscription(subscriptionId: SubscriptionId) {
        deletedSubscriptions.add(subscriptionId)
    }

    override suspend fun deleteSubscriptions(subscriptionIds: List<SubscriptionId>) {
        deletedSubscriptions.addAll(subscriptionIds)
    }
}
