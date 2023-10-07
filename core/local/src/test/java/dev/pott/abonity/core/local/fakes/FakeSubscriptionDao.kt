package dev.pott.abonity.core.local.fakes

import dev.pott.abonity.core.local.db.SubscriptionDao
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

class FakeSubscriptionDao(
    initialEntities: List<SubscriptionEntity> = emptyList()
) : SubscriptionDao {

    private val entities: Channel<List<SubscriptionEntity>> = Channel(
        capacity = Channel.UNLIMITED
    )

    init {
        entities.trySendBlocking(initialEntities)
    }

    override fun upsertSubscription(subscription: SubscriptionEntity) {
        val currentEntities = entities.tryReceive().getOrThrow().toMutableList()
        val indexOfEntity =
            currentEntities.indexOfFirst { it.id == subscription.id }
        if (indexOfEntity != -1) {
            currentEntities[indexOfEntity] = subscription
        } else {
            currentEntities.add(subscription)
        }
        entities.trySendBlocking(currentEntities)
    }

    override fun getSubscriptionFlow(): Flow<List<SubscriptionEntity>> {
        return entities.consumeAsFlow()
    }
}
