package dev.pott.abonity.core.test.subscription.entities

import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId

fun createTestCategory(id: Long = 1, name: String = "Test Category"): Category =
    Category(
        CategoryId(id),
        name,
    )

fun createTestCategories(
    count: Int,
    idStart: Long = 0,
    nameStart: String = "Test Category",
): List<Category> =
    (1..count).map {
        createTestCategory(idStart + it, "$nameStart $it")
    }
