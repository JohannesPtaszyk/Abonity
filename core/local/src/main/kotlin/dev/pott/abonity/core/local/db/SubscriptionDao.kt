package dev.pott.abonity.core.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Upsert
    suspend fun upsertSubscription(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscription_entity")
    fun getSubscriptionFlow(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscription_entity WHERE id==:id")
    fun getSubscription(id: Long): Flow<SubscriptionEntity>
}
