package dev.pott.abonity.core.local.fakes

import dev.pott.abonity.core.local.subscription.db.CategoryDao
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCategoryDao(
    initialEntities: List<CategoryEntity> = emptyList(),
) : CategoryDao {

    private val entities = MutableStateFlow(initialEntities)

    override suspend fun upsert(category: CategoryEntity): Long {
        return category.id
    }

    override fun getCategoriesFlow(): Flow<List<CategoryEntity>> {
        return entities
    }

    override suspend fun delete(ids: List<Long>) {
        entities.value = entities.value.filter { it.id !in ids }
    }
}
