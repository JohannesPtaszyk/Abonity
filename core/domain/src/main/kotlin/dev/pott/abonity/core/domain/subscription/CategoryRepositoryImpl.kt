package dev.pott.abonity.core.domain.subscription

import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val localDataSource: CategoryLocalDataSource,
) : CategoryRepository {
    override suspend fun addOrUpdateCategory(category: Category): Category {
        return localDataSource.addOrUpdateCategory(category)
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return localDataSource.getCategoriesFlow()
    }

    override suspend fun deleteCategory(categoryIds: List<CategoryId>) {
        localDataSource.deleteCategory(categoryIds)
    }
}
