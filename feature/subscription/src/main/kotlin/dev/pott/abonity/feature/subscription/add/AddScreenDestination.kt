package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.NestedDestination

private const val ADD_ROUTE = "add"
private const val SUBSCRIPTION_ID_KEY = "subscription_id"
private const val NO_SUBSCRIPTION_PASSED_ID = -1L

object AddScreenDestination : NestedDestination<AddScreenDestination.Args>(
    SubscriptionNavigationDestination,
    ADD_ROUTE,
) {

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(SUBSCRIPTION_ID_KEY) {
            type = NavType.LongType
            defaultValue = NO_SUBSCRIPTION_PASSED_ID
        },
    )

    override fun getArgs(savedStateHandle: SavedStateHandle): Args {
        val subscriptionId = savedStateHandle.get<Long>(SUBSCRIPTION_ID_KEY)?.takeIf {
            it != NO_SUBSCRIPTION_PASSED_ID
        }
        return Args(subscriptionId)
    }

    override fun getParamsFromArgs(args: Args): Map<String, Any> {
        return buildMap {
            put(SUBSCRIPTION_ID_KEY, args.subscriptionId ?: NO_SUBSCRIPTION_PASSED_ID)
        }
    }

    data class Args(val subscriptionId: Long? = null)
}

fun NavController.navigateToAddScreen() {
    navigate(
        AddScreenDestination.getRouteWithArgs(
            AddScreenDestination.Args(),
        ),
    )
}
