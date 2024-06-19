package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val localDataSource: SubscriptionLocalDataSource,
) : SubscriptionRepository {

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription =
        localDataSource.addOrUpdateSubscription(subscription)

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> =
        localDataSource.getSubscriptionsFlow()

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription?> =
        localDataSource.getSubscriptionFlow(subscriptionId)

    override suspend fun deleteSubscription(subscriptionId: SubscriptionId) {
        localDataSource.deleteSubscription(subscriptionId)
    }

    override suspend fun deleteSubscriptions(subscriptionIds: List<SubscriptionId>) {
        localDataSource.deleteSubscriptions(subscriptionIds)
    }
}
