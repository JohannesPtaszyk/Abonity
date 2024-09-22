package dev.pott.abonity.feature.subscription

import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.pott.abonity.core.navigation.coreNavTypeMap
import dev.pott.abonity.feature.subscription.add.AddDestination
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.navigateToAddDestination
import dev.pott.abonity.feature.subscription.category.CategoryDestination
import dev.pott.abonity.feature.subscription.category.CategoryScreen
import dev.pott.abonity.feature.subscription.category.navigateToCategoryDestination
import dev.pott.abonity.feature.subscription.overview.OverviewDestination
import dev.pott.abonity.feature.subscription.overview.OverviewRoute

fun NavGraphBuilder.subscriptionGraph(
    state: SubscriptionGraphState,
    navController: NavController,
    promptAppStoreReview: () -> Unit,
) {
    navigation<SubscriptionNavigationDestination>(
        startDestination = OverviewDestination(),
        typeMap = coreNavTypeMap,
    ) {
        composable<OverviewDestination>(typeMap = coreNavTypeMap) { backStackEntry ->
            OverviewRoute(
                showAsMultiColumn = state.showOverviewAsMultiColumn,
                onEditClick = { navController.navigateToAddDestination(it) },
                onOpenCategoriesClick = { navController.navigateToCategoryDestination() },
                args = remember { backStackEntry.toRoute<OverviewDestination>() },
            )
        }

        composable<CategoryDestination> {
            CategoryScreen(close = { navController.popBackStack() })
        }
    }
    dialog<AddDestination>(
        typeMap = coreNavTypeMap,
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        AddScreen(
            close = {
                navController.popBackStack()
            },
            promptAppStoreReview = promptAppStoreReview,
        )
    }
}
