package dev.pott.abonity.core.local.fakes

import dev.pott.abonity.core.local.db.SubscriptionDao
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSubscriptionDao(
    initialEntities: List<SubscriptionEntity> = emptyList(),
) : SubscriptionDao {
    private val entities: Channel<List<SubscriptionEntity>> =
        Channel(
            capacity = Channel.UNLIMITED,
        )

    init {
        entities.trySendBlocking(initialEntities)
    }

    override suspend fun upsertSubscription(subscription: SubscriptionEntity): Long {
        val currentEntities = entities.tryReceive().getOrThrow().toMutableList()
        val indexOfEntity =
            currentEntities.indexOfFirst { it.id == subscription.id }
        if (indexOfEntity != -1) {
            currentEntities[indexOfEntity] = subscription
        } else {
            currentEntities.add(subscription)
        }
        entities.trySendBlocking(currentEntities)
        return currentEntities.size.toLong()
    }

    override fun getSubscriptionsFlow(): Flow<List<SubscriptionEntity>> {
        return entities.receiveAsFlow()
    }

    override fun getSubscriptionFlow(id: Long): Flow<SubscriptionEntity> {
        return entities.receiveAsFlow()
            .mapNotNull {
                    subscriptionEntities ->
                subscriptionEntities.find { it.id == id }
            }
    }
}
