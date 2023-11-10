package dev.pott.abonity.feature.subscription.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.pott.abonity.feature.subscription.SubscriptionNavigationDestination
import dev.pott.abonity.navigation.destination.NestedDestination

private const val DETAIL_ROUTE = "detail"
private const val DETAIL_ID_KEY = "detail_id"
private const val NO_SUBSCRIPTION_PASSED_ID = -1L

object DetailScreenDestination : NestedDestination<DetailScreenDestination.Args>(
    SubscriptionNavigationDestination,
    DETAIL_ROUTE,
) {
    override val arguments: List<NamedNavArgument> =
        listOf(
            navArgument(DETAIL_ID_KEY) { type = NavType.LongType },
        )

    override fun getArgs(savedStateHandle: SavedStateHandle): Args {
        val id = savedStateHandle.get<Long>(DETAIL_ID_KEY)?.takeIf {
            it != NO_SUBSCRIPTION_PASSED_ID
        }
        return Args(id)
    }

    override fun getParamsFromArgs(args: Args): Map<String, Any> {
        return mapOf(
            DETAIL_ID_KEY to (args.subscriptionId ?: NO_SUBSCRIPTION_PASSED_ID),
        )
    }

    data class Args(val subscriptionId: Long?)
}
