package dev.pott.abonity.feature.subscription.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.pott.abonity.core.entity.subscription.Category
import dev.pott.abonity.core.ui.components.navigation.BackButton
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CategoryScreen(
    close: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryScreen(
        close = close,
        selectCategory = viewModel::selectCategory,
        deleteSelected = viewModel::deleteSelected,
        state = state,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategoryScreen(
    close: () -> Unit,
    selectCategory: (Category) -> Unit,
    deleteSelected: () -> Unit,
    state: CategoryState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Categories") },
                navigationIcon = {
                    BackButton(onClick = close)
                },
                actions = {
                    FilledTonalButton(
                        onClick = deleteSelected,
                        enabled = state is CategoryState.Loaded && state.deleteEnabled,
                    ) {
                        if (state is CategoryState.Loaded && state.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(8.dp))
                        } else {
                            Text(text = "Delete")
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        when (state) {
            is CategoryState.Loaded -> {
                FlowRow(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.categories.forEach { category ->
                        InputChip(
                            selected = state.selectedCategories.contains(category),
                            onClick = { selectCategory(category) },
                            label = { Text(text = category.name) },
                        )
                    }
                }
            }

            is CategoryState.Loading -> {
                Box(modifier = Modifier.padding(paddingValues)) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview
@Composable
fun CategoryScreenPreview() {
    val categories = buildList {
        repeat(10) { add(Category(name = "Category $it")) }
    }.toImmutableList()
    CategoryScreen(
        close = {},
        selectCategory = {},
        deleteSelected = {},
        state = CategoryState.Loaded(
            categories = categories,
            selectedCategories = categories.take(2).toImmutableList(),
            deleteEnabled = true,
            isDeleting = false,
        ),
    )
}
