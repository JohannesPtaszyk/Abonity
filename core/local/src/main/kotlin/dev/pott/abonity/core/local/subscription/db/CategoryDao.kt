package dev.pott.abonity.core.local.subscription.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.pott.abonity.core.local.subscription.db.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun upsert(category: CategoryEntity): Long

    @Query("SELECT * FROM category_entity")
    fun getCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("DELETE FROM category_entity WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)
}
