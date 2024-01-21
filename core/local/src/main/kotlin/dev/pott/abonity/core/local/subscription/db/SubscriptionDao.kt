package dev.pott.abonity.core.local.subscription.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Upsert
    suspend fun upsertSubscription(subscription: SubscriptionEntity): Long

    @Query("SELECT * FROM subscription_entity")
    fun getSubscriptionsFlow(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscription_entity WHERE id==:id")
    fun getSubscriptionFlow(id: Long): Flow<SubscriptionEntity?>

    @Query("DELETE FROM subscription_entity WHERE id==:id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM subscription_entity WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)
}
