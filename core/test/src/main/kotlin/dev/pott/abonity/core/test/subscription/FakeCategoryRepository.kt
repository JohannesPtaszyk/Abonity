package dev.pott.abonity.core.test.subscription

import dev.pott.abonity.core.domain.subscription.CategoryRepository
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCategoryRepository(
    private val testCategoriesFlow: Flow<List<Category>> = flowOf(emptyList()),
) : CategoryRepository {

    val addedCategories = mutableListOf<Category>()
    val deletedCategories = mutableListOf<CategoryId>()

    override suspend fun addOrUpdateCategory(category: Category): Category {
        addedCategories.add(category)
        return category
    }

    override fun getCategoriesFlow(): Flow<List<Category>> = testCategoriesFlow

    override suspend fun deleteCategory(categoryIds: List<CategoryId>) {
        deletedCategories.addAll(categoryIds)
    }
}
