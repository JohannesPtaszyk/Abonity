package dev.pott.abonity.core.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.pott.abonity.core.local.db.entities.SubscriptionEntity

@Database(
    version = 1,
    entities = [
        SubscriptionEntity::class,
    ],
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}
