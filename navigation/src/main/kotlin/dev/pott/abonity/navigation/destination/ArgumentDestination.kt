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
            appendArguments()
        }

    fun routeWithArgs(args: T): String {
        val params = args.toMap()
        return if (params.isEmpty()) {
            route
        } else {
            replaceParamsInRoute(params)
        }
    }

    private fun replaceParamsInRoute(params: Map<String, Any>): String {
        var route = this.route
        params.forEach {
            route = route.replace("{${it.key}}", it.value.toString())
        }
        return route
    }

    private fun StringBuilder.appendArguments() {
        requiredArguments.forEach {
            append("/{${it.name}}")
        }
        optionalArguments.forEachIndexed { i: Int, argument: NamedNavArgument ->
            if (i == 0) {
                append("?${argument.name}={${argument.name}}")
            } else {
                append("$${argument.name}={${argument.name}}")
            }
        }
    }
}
