package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.activity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.pott.abonity.feature.home.homeGraph
import dev.pott.abonity.feature.legal.consent.consentGraph
import dev.pott.abonity.feature.legal.consent.navigateToConsent
import dev.pott.abonity.feature.settings.main.settingsNavGraph
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.feature.subscription.add.navigateToAddDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph

private const val OSS_LICENSE_ACTIVITY_ROUTE = "ossLicenses"

fun NavGraphBuilder.appNavGraph(
    state: AppState,
    navController: NavController,
    openNotificationSettings: () -> Unit,
    openUrl: (String) -> Unit,
) {
    homeGraph(
        openDetails = { subscriptionId ->
            navController.navigate(
                SubscriptionNavigationDestination(subscriptionId),
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
    subscriptionGraph(state.subscriptionGraphState, navController)
    settingsNavGraph(
        openOssLicenses = { navController.navigate(OSS_LICENSE_ACTIVITY_ROUTE) },
        openNotificationSettings = openNotificationSettings,
        openConsentDialog = { navController.navigateToConsent() },
        openUrl = openUrl,
        nestedGraphs = {
            consentGraph(
                close = { navController.popBackStack() },
                openUrl = openUrl,
            )
        },
    )
    activity(OSS_LICENSE_ACTIVITY_ROUTE) { activityClass = OssLicensesMenuActivity::class }
}
