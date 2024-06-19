package dev.pott.abonity.core.local.subscription

import dev.pott.abonity.core.domain.subscription.SubscriptionLocalDataSource
import dev.pott.abonity.core.entity.subscription.Subscription
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.local.subscription.db.SubscriptionDao
import dev.pott.abonity.core.local.subscription.mapper.toDomain
import dev.pott.abonity.core.local.subscription.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomSubscriptionDataSource @Inject constructor(private val dao: SubscriptionDao) :
    SubscriptionLocalDataSource {

    override suspend fun addOrUpdateSubscription(subscription: Subscription): Subscription {
        val id = dao.upsertSubscriptionCategory(subscription.toEntity())
        return subscription.copy(id = SubscriptionId(id))
    }

    override fun getSubscriptionsFlow(): Flow<List<Subscription>> =
        dao.getSubscriptionsFlow().map { subscriptions ->
            subscriptions.map { entity ->
                entity.toDomain()
            }
        }

    override fun getSubscriptionFlow(subscriptionId: SubscriptionId): Flow<Subscription?> =
        dao.getSubscriptionFlow(subscriptionId.value).map {
            it?.toDomain()
        }

    override suspend fun deleteSubscription(subscriptionId: SubscriptionId) {
        dao.delete(subscriptionId.value)
    }

    override suspend fun deleteSubscriptions(subscriptionIds: List<SubscriptionId>) {
        dao.delete(subscriptionIds.map(SubscriptionId::value))
    }
}
