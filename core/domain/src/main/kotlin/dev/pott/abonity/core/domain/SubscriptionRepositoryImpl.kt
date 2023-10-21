package dev.pott.abonity.core.domain

import dev.pott.abonity.core.entity.Subscription
import dev.pott.abonity.core.entity.SubscriptionId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val localDataSource: SubscriptionLocalDataSource,
) : SubscriptionRepository {
    override fun getSubscriptionFlow(): Flow<List<Subscription>> {
        return localDataSource.getSubscriptionFlow()
    }

    override fun getSubscription(subscriptionId: SubscriptionId): Flow<Subscription> {
        return localDataSource.getSubscription(subscriptionId)
    }
}
