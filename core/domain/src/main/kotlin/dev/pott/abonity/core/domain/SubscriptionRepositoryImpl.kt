package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val localDataSource: SubscriptionLocalDataSource,
) : SubscriptionRepository {

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription {
        return localDataSource.addOrUpdateSubscription(subscription)
    }

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> {
        return localDataSource.getSubscriptionsFlow()
    }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription> {
        return localDataSource.getSubscriptionFlow(subscriptionId)
    }
}
