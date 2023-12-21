package dev.pott.abonity.feature.subscription.overview

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.pott.abonity.core.entity.subscription.SubscriptionId
import dev.pott.abonity.core.ui.navigation.AppDeeplink
import dev.pott.abonity.navigation.destination.ArgumentDestination
import dev.pott.abonity.navigation.destination.Arguments
import dev.pott.abonity.navigation.destination.Deeplinkable
import dev.pott.abonity.navigation.destination.SavedStateArgumentParser
import dev.pott.abonity.navigation.destination.createDeeplinkRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val OVERVIEW_ROUTE = "overview"
private const val DEEPLINK_DETAIL_PATH_POSTFIX = "detail"

object OverviewScreenDestination :
    ArgumentDestination<OverviewScreenDestination.Args>,
    Deeplinkable {

    override val baseRoute: String = OVERVIEW_ROUTE

    override val optionalArguments: ImmutableList<NamedNavArgument> = persistentListOf(
        navArgument(Args.DETAIL_ID_KEY) {
            type = NavType.LongType
            defaultValue = -1L
        },
    )

    override val deeplinks: ImmutableList<NavDeepLink> = persistentListOf(
        navDeepLink {
            uriPattern = createDeeplinkRoute(
                AppDeeplink.HOST,
                AppDeeplink.SCHEME,
                DEEPLINK_DETAIL_PATH_POSTFIX,
            )
        },
    )

    data class Args(val detailId: SubscriptionId?) : Arguments {

        override fun toMap(): Map<String, Any> {
            return buildMap {
                detailId?.let { put(DETAIL_ID_KEY, it.value) }
            }
        }

        companion object : SavedStateArgumentParser<Args> {
            const val DETAIL_ID_KEY = "detail_id"
            override fun parse(savedStateHandle: SavedStateHandle): Args {
                val subscriptionId = savedStateHandle.get<Long>(DETAIL_ID_KEY)?.takeIf {
                    it != -1L
                }?.let { SubscriptionId(it) }
                return Args(subscriptionId)
            }
        }
    }
}
