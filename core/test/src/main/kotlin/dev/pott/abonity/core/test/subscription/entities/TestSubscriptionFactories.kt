package dev.pott.abonity.core.test.subscription.entities

import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId

fun createTestCategory(id: Long = 1, name: String = "Test Category"): Category {
    return Category(
        CategoryId(id),
        name,
    )
}

fun createTestCategories(
    count: Int,
    idStart: Long = 1,
    nameStart: String = "Test Category",
): List<Category> {
    return (1..count).map {
        createTestCategory(idStart + it, "$nameStart $it")
    }
}
