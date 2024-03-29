package dev.pott.abonity.core.local.fakes

import dev.pott.abonity.core.local.subscription.db.SubscriptionDao
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryCrossRef
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class FakeSubscriptionDao(
    initialEntities: List<SubscriptionCategoryEntity> = emptyList(),
) : SubscriptionDao {

    private val entities = MutableStateFlow(initialEntities)
    private val crossRefs = MutableStateFlow(emptyList<SubscriptionCategoryCrossRef>())

    override suspend fun upsertSubscription(subscription: SubscriptionEntity): Long {
        val index = entities.value.indexOfFirst { it.subscription.id == subscription.id }
        if (index == -1) {
            entities.update { it + SubscriptionCategoryEntity(subscription, emptyList()) }
        } else {
            entities.update {
                it.toMutableList()
                    .apply { set(index, SubscriptionCategoryEntity(subscription, emptyList())) }
            }
        }
        return subscription.id
    }

    override suspend fun insertSubscriptionCategoryCrossRefs(
        subscriptionCategoryCrossRefs: List<SubscriptionCategoryCrossRef>,
    ) {
        crossRefs.update { it + subscriptionCategoryCrossRefs }
    }

    override fun getSubscriptionsFlow(): Flow<List<SubscriptionCategoryEntity>> {
        return entities
    }

    override fun getSubscriptionFlow(id: Long): Flow<SubscriptionCategoryEntity?> {
        return entities.mapNotNull { subscriptionCategoryEntities ->
            subscriptionCategoryEntities.find { it.subscription.id == id }
        }
    }

    override suspend fun delete(id: Long) {
        entities.update { it.filterNot { it.subscription.id == id } }
    }

    override suspend fun delete(ids: List<Long>) {
        entities.update { it.filterNot { ids.contains(it.subscription.id) } }
    }
}
