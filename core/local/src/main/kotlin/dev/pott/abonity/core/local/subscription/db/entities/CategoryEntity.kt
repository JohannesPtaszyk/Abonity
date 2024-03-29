package dev.pott.abonity.core.local.subscription.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "category_entity",
    indices = [Index(value = ["name"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("name")
    val name: String,
)
