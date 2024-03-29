package dev.pott.abonity.core.local.subscription.db.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class SubscriptionCategoryEntity(
    @Embedded val subscription: SubscriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            SubscriptionCategoryCrossRef::class,
            parentColumn = "subscription_id",
            entityColumn = "category_id",
        ),
    )
    val categories: List<CategoryEntity>,
)
