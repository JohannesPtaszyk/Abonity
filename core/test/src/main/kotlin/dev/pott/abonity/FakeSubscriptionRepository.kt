package dev.pott.abonity

import dev.pott.abonity.core.domain.SubscriptionRepository
import dev.pott.abonity.core.entity.Subscription
import kotlinx.coroutines.flow.Flow

class FakeSubscriptionRepository(
    private val subscriptionFlow: Flow<List<Subscription>>
) : SubscriptionRepository {
    override fun getSubscriptionFlow(): Flow<List<Subscription>> {
        return subscriptionFlow
    }
}
