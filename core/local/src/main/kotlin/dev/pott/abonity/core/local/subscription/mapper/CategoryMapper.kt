package dev.pott.abonity.core.local.subscription.mapper

import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity

fun Category.toEntity(): CategoryEntity = CategoryEntity(id.value, name)

fun CategoryEntity.toDomain(): Category = Category(CategoryId(id), name)
