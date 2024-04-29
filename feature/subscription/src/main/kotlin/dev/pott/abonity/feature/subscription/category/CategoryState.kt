package dev.pott.abonity.feature.subscription.category

import dev.pott.abonity.core.entity.subscription.Category
import kotlinx.collections.immutable.ImmutableList

sealed interface CategoryState {
    data object Loading : CategoryState
    data class Loaded(
        val categories: ImmutableList<Category>,
        val selectedCategories: ImmutableList<Category>,
        val deleteEnabled: Boolean,
        val isDeleting: Boolean,
    ) : CategoryState
}
