package dev.pott.abonity.feature.subscription.add

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.pott.abonity.core.entity.SubscriptionId
import dev.pott.abonity.navigation.destination.ArgumentDestination
import dev.pott.abonity.navigation.destination.Arguments
import dev.pott.abonity.navigation.destination.SavedStateArgumentParser
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val ADD_ROUTE = "add"
private const val SUBSCRIPTION_ID_KEY = "subscription_id"
private const val NO_SUBSCRIPTION_PASSED_ID = -1L

object AddScreenDestination : ArgumentDestination<AddScreenDestination.Args> {

    override val baseRoute: String = ADD_ROUTE

    override val optionalArguments: ImmutableList<NamedNavArgument> = persistentListOf(
        navArgument(SUBSCRIPTION_ID_KEY) {
            type = NavType.LongType
            defaultValue = NO_SUBSCRIPTION_PASSED_ID
        },
    )

    data class Args(val subscriptionId: SubscriptionId? = null) : Arguments {
        override fun toMap(): Map<String, Any> = buildMap {
            if (subscriptionId != null) {
                put(SUBSCRIPTION_ID_KEY, subscriptionId.value)
            }
        }

        companion object : SavedStateArgumentParser<Args> {
            override fun parse(savedStateHandle: SavedStateHandle): Args {
                val subscriptionId = savedStateHandle.get<Long>(SUBSCRIPTION_ID_KEY)?.takeIf {
                    it != NO_SUBSCRIPTION_PASSED_ID
                }?.let {
                    SubscriptionId(it)
                }
                return Args(subscriptionId)
            }
        }
    }
}

fun NavController.navigateToAddScreen(subscriptionId: SubscriptionId? = null) {
    navigate(
        AddScreenDestination.routeWithArgs(
            AddScreenDestination.Args(subscriptionId),
        ),
    )
}
