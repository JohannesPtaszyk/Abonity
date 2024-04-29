package dev.pott.abonity.core.entity.subscription

data class Category(
    val id: CategoryId = CategoryId.none(),
    val name: String,
)
