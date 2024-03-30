package dev.pott.abonity.feature.subscription.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pott.abonity.core.domain.subscription.CategoryRepository
import dev.pott.abonity.core.entity.subscription.Category
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val selectedCategories = MutableStateFlow(emptyList<Category>())
    private val isDeleting = MutableStateFlow(false)

    val state: StateFlow<CategoryState> = combine(
        categoryRepository.getCategoriesFlow(),
        selectedCategories,
        isDeleting,
    ) { categories, selectedCategories, isDeleting ->
        CategoryState.Loaded(
            categories = categories.toImmutableList(),
            selectedCategories = selectedCategories.toImmutableList(),
            deleteEnabled = selectedCategories.isNotEmpty() && !isDeleting,
            isDeleting = isDeleting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryState.Loading,
    )

    fun selectCategory(category: Category) {
        val currentCategories = selectedCategories.value
        selectedCategories.value = if (currentCategories.contains(category)) {
            currentCategories - category
        } else {
            currentCategories + category
        }
    }

    fun deleteSelected() {
        isDeleting.value = true
        viewModelScope.launch {
            val currentCategories = selectedCategories.value
            if (currentCategories.isNotEmpty()) {
                categoryRepository.deleteCategory(currentCategories.map { it.id })
                selectedCategories.value = emptyList()
            }
            isDeleting.value = false
        }
    }
}
