package dev.pott.abonity.core.test.subscription

import dev.pott.abonity.core.domain.subscription.CategoryLocalDataSource
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeCategoryLocalDataSource(
    private val testCategoriesFlow: Flow<List<Category>> = emptyFlow(),
) : CategoryLocalDataSource {

    val addedCategories = mutableListOf<Category>()
    val deletedCategories = mutableListOf<CategoryId>()

    override suspend fun addOrUpdateCategory(category: Category): Category {
        addedCategories.add(category)
        return category
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return testCategoriesFlow
    }

    override suspend fun deleteCategory(categoryId: CategoryId) {
        deletedCategories.add(categoryId)
    }
}
