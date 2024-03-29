package dev.pott.abonity.core.local.subscription

import dev.pott.abonity.core.domain.subscription.CategoryLocalDataSource
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.entity.subscription.CategoryId
import dev.pott.abonity.core.local.subscription.db.CategoryDao
import dev.pott.abonity.core.local.subscription.mapper.toDomain
import dev.pott.abonity.core.local.subscription.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCategoryDataSource @Inject constructor(
    private val dao: CategoryDao,
) : CategoryLocalDataSource {
    override suspend fun addOrUpdateCategory(category: Category): Category {
        val id = dao.upsert(category.toEntity())
        return category.copy(id = CategoryId(id))
    }

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return dao.getCategoriesFlow().map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun deleteCategory(categoryId: CategoryId) {
        dao.delete(categoryId.value)
    }
}
