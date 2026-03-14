package dev.pott.abonity.core.local.subscription.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionCategoryCrossRef
import dev.pott.abonity.core.local.subscription.db.entities.SubscriptionEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE subscription_entity ADD COLUMN notification_days_before INTEGER DEFAULT NULL",
        )
    }
}

@Database(
    version = 2,
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
