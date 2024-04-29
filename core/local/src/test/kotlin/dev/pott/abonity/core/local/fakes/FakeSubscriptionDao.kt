package dev.pott.abonity.core.local.fakes

import dev.pott.abonity.core.local.subscription.db.SubscriptionDao
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class FakeSubscriptionDao(
    initialEntities: List<SubscriptionEntity> = emptyList(),
) : SubscriptionDao {

    private val entities: MutableStateFlow<List<SubscriptionEntity>> =
        MutableStateFlow(initialEntities)

    override suspend fun upsertSubscription(subscription: SubscriptionEntity): Long {
        val currentEntities = entities.value.toMutableList()
        val indexOfEntity = currentEntities.indexOfFirst { it.id == subscription.id }
        if (indexOfEntity != -1) {
            currentEntities[indexOfEntity] = subscription
        } else {
            currentEntities.add(subscription)
        }
        entities.value = currentEntities
        return currentEntities.size.toLong()
    }

    override fun getSubscriptionsFlow(): Flow<List<SubscriptionEntity>> {
        return entities
    }

    override fun getSubscriptionFlow(id: Long): Flow<SubscriptionEntity> {
        return entities.mapNotNull { subscriptionEntities ->
            subscriptionEntities.find { it.id == id }
        }
    }

    override suspend fun delete(ids: List<Long>) {
        entities.update { currentEntities ->
            val indexOfId = currentEntities.filter { ids.contains(it.id) }.toSet()
            currentEntities - indexOfId
        }
    }

    override suspend fun delete(id: Long) {
        entities.update { currentEntities ->
            val indexOfId = currentEntities.first { it.id == id }
            currentEntities - indexOfId
        }
    }
}
