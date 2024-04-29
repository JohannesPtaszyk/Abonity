package dev.pott.abonity.core.local.subscription.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryCrossRef
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity

@Database(
    version = 1,
    entities = [
        SubscriptionEntity::class,
        CategoryEntity::class,
        SubscriptionCategoryCrossRef::class,
    ],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun categoryDao(): CategoryDao
}
