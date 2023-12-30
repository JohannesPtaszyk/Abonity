package dev.pott.abonity.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.activity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.pott.abonity.feature.home.homeGraph
import dev.pott.abonity.feature.settings.main.settingsNavGraph
import dev.pott.abonity.feature.subscription.overview.OverviewScreenDestination
import dev.pott.abonity.feature.subscription.subscriptionGraph
import dev.pott.abonity.navigation.destination.setDestination

private const val OSS_LICENSE_ACTIVITY_ROUTE = "ossLicenses"

fun NavGraphBuilder.appNavGraph(
    state: AppState,
    navController: NavController,
    openNotificationSettings: () -> Unit,
) {
    homeGraph(
        openDetails = { subscriptionId ->
            navController.createDeepLink()
                .setDestination(
                    OverviewScreenDestination,
                    OverviewScreenDestination.Args(subscriptionId),
                )
                .createPendingIntent()
                .send()
        },
        openSubscriptions = { navController.navigateTabItem(NavigationItem.SUBSCRIPTION) },
        openNotificationSettings = openNotificationSettings,
    )
    subscriptionGraph(state.subscriptionGraphState, navController)
    settingsNavGraph(
        openOssLicenses = { navController.navigate(OSS_LICENSE_ACTIVITY_ROUTE) },
        openNotificationSettings = openNotificationSettings,
    )
    activity(OSS_LICENSE_ACTIVITY_ROUTE) { activityClass = OssLicensesMenuActivity::class }
}
