package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.pott.abonity.app.licenses.OssLicencesDestination
import dev.pott.abonity.app.licenses.OssLicensesScreen
import dev.pott.abonity.feature.home.homeGraph
import dev.pott.abonity.feature.legal.consent.consentGraph
import dev.pott.abonity.feature.legal.consent.navigateToConsent
import dev.pott.abonity.feature.settings.main.settingsNavGraph
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph

fun NavGraphBuilder.appNavGraph(
    state: AppState,
    navController: NavController,
    openNotificationSettings: () -> Unit,
    promptAppStoreReview: () -> Unit,
) {
    homeGraph(
        openDetails = { subscriptionId ->
            navController.navigate(
                SubscriptionNavigationDestination(subscriptionId.value),
            ) {
                val startDestination = navController.graph.findStartDestination()
                popUpTo(startDestination.id) { saveState = true }
                launchSingleTop = true
                restoreState = false
            }
        },
        openSubscriptions = { navController.navigateTabItem(NavigationItem.SUBSCRIPTION) },
        openNotificationSettings = openNotificationSettings,
        openAddScreen = { navController.navigateToAddDestination() },
    )

    subscriptionGraph(state.subscriptionGraphState, navController, promptAppStoreReview)

    settingsNavGraph(
        openOssLicenses = { navController.navigate(OssLicencesDestination) },
        openNotificationSettings = openNotificationSettings,
        openConsentDialog = { navController.navigateToConsent() },
        nestedGraphs = {
            consentGraph(
                close = { navController.popBackStack() },
            )
            composable<OssLicencesDestination> {
                OssLicensesScreen(navigateUp = { navController.navigateUp() })
            }
        },
    )
}
