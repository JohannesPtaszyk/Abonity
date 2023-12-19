package dev.pott.abonity.navigation.destination

import androidx.navigation.NamedNavArgument
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface ArgumentDestination<T : Arguments> : Destination {

    val baseRoute: String

    val requiredArguments: ImmutableList<NamedNavArgument>
        get() = persistentListOf()

    val optionalArguments: ImmutableList<NamedNavArgument>
        get() = persistentListOf()

    override val route: String
        get() = buildString {
            append(baseRoute)
            appendArguments(this@ArgumentDestination)
        }

    fun routeWithArgs(args: T): String {
        val params = args.toMap()
        return if (params.isEmpty()) {
            route
        } else {
            route.replaceNavArguments(params)
        }
    }
}
