package dev.pott.abonity.core.test

import dev.pott.abonity.core.domain.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeSubscriptionLocalDataSource(
    private val testSubscriptionsFlow: Flow<List<Subscription>> = emptyFlow(),
    private val testSubscriptionFlow: Flow<Subscription> = emptyFlow(),
) : SubscriptionLocalDataSource {
    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return testSubscriptionsFlow
    }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription> {
        return testSubscriptionFlow
    }
}
