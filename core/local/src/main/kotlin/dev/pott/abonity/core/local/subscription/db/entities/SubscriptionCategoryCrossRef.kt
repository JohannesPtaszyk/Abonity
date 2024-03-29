package dev.pott.abonity.core.local.subscription.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "subscription_category_cross_ref",
    primaryKeys = ["subscription_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = SubscriptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["subscription_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class SubscriptionCategoryCrossRef(
    @ColumnInfo("subscription_id")
    val subscriptionId: Long,
    @ColumnInfo("category_id", index = true)
    val categoryId: Long,
)
