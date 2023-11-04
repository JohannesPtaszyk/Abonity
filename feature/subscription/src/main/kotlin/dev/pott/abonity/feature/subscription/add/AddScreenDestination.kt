package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.pott.abonity.core.entity.SubscriptionId
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
            nullable = false
            defaultValue = NO_SUBSCRIPTION_PASSED_ID
        },
    )

    override fun getArgs(savedStateHandle: SavedStateHandle): Args {
        val subscriptionId = savedStateHandle.get<Long>(SUBSCRIPTION_ID_KEY)?.takeIf {
            it != NO_SUBSCRIPTION_PASSED_ID
        }?.let {
            SubscriptionId(it)
        }
        return Args(subscriptionId)
    }

    override fun getParamsFromArgs(args: Args): Map<String, Any> {
        return buildMap {
            args.subscriptionId?.let { id -> put(SUBSCRIPTION_ID_KEY, id) }
        }
    }

    data class Args(val subscriptionId: SubscriptionId?)
}
