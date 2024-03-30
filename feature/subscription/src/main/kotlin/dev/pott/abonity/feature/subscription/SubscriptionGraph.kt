package dev.pott.abonity.feature.subscription

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import dev.pott.abonity.feature.subscription.add.AddScreen
import dev.pott.abonity.feature.subscription.add.AddScreenDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddScreen
import dev.pott.abonity.feature.subscription.category.CategoryDestination
import dev.pott.abonity.feature.subscription.category.CategoryScreen
import dev.pott.abonity.feature.subscription.category.navigateToCategory
import dev.pott.abonity.feature.subscription.overview.OverviewRoute
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.navigation.destination.composable
import dev.pott.abonity.navigation.destination.edgeToEdgeDialog
import dev.pott.abonity.navigation.destination.navigation

fun NavGraphBuilder.subscriptionGraph(
    state: SubscriptionGraphState,
    navController: NavController,
) {
    navigation(
        destination = SubscriptionNavigationDestination,
        startDestination = OverviewScreenDestination,
    ) {
        composable(OverviewScreenDestination) { backStackEntry ->
            OverviewRoute(
                showAsMultiColumn = state.showOverviewAsMultiColumn,
                onEditClick = { navController.navigateToAddScreen(it) },
                onOpenCategoriesClick = { navController.navigateToCategory() },
                args = backStackEntry.arguments?.let { OverviewScreenDestination.Args.parse(it) },
            )
        }

        composable(CategoryDestination) {
            CategoryScreen(close = { navController.popBackStack() })
        }
    }
    edgeToEdgeDialog(
        AddScreenDestination,
    ) {
        AddScreen(close = { navController.popBackStack() })
    }
}
