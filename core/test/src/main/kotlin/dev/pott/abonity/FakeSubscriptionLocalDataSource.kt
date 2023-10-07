package dev.pott.abonity

import dev.pott.abonity.core.domain.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.Flow

class FakeSubscriptionLocalDataSource(
    private val testSubscriptionFlow: Flow<List<Subscription>>
) : SubscriptionLocalDataSource {
    override fun getSubscriptionFlow(): Flow<List<Subscription>> {
        return testSubscriptionFlow
    }
}
