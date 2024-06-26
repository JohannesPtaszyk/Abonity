package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    suspend fun addOrUpdateCategory(category: Category): Category
    fun getCategoriesFlow(): Flow<List<Category>>
    suspend fun deleteCategory(categoryIds: List<CategoryId>)
}
