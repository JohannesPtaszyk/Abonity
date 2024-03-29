package dev.pott.abonity.core.local.subscription.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryCrossRef
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Upsert
    suspend fun upsertSubscription(subscription: SubscriptionEntity): Long

    @Transaction
    suspend fun upsertSubscriptionCategory(subscription: SubscriptionCategoryEntity): Long {
        val id = upsertSubscription(subscription.subscription)
            .takeIf { it > 0 }
            ?: subscription.subscription.id
        val crossRefs = subscription.categories.map { SubscriptionCategoryCrossRef(id, it.id) }
        insertSubscriptionCategoryCrossRefs(crossRefs)
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubscriptionCategoryCrossRefs(
        subscriptionCategoryCrossRefs: List<SubscriptionCategoryCrossRef>,
    )

    @Transaction
    @Query("SELECT * FROM subscription_entity")
    fun getSubscriptionsFlow(): Flow<List<SubscriptionCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM subscription_entity WHERE id==:id")
    fun getSubscriptionFlow(id: Long): Flow<SubscriptionCategoryEntity?>

    @Query("DELETE FROM subscription_entity WHERE id==:id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM subscription_entity WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)
}
