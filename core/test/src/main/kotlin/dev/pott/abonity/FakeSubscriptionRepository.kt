package dev.pott.abonity

import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeSubscriptionRepository(
    private val subscriptionsFlow: Flow<List<Subscription>> = emptyFlow(),
    private val subscriptionFlow: Flow<Subscription> = emptyFlow()
) : SubscriptionRepository {

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return subscriptionsFlow
    }

    override fun getSubscription(subscriptionId: SubscriptionId): Flow<Subscription> {
        return subscriptionFlow
    }
}
