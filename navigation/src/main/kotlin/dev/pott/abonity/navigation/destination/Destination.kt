package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import co.touchlab.kermit.Logger

abstract class Destination<T>(val baseRoute: String) {

    open val route: String by lazy {
        buildString {
            append(baseRoute)
            appendArguments()
        }.also {
            Logger.withTag(this::class.java.simpleName).v {
                "Created route: $it for destination: $this"
            }
        }
    }

    open val deeplinks: List<NavDeepLink> = emptyList()

    abstract val arguments: List<NamedNavArgument>

    abstract fun getArgs(savedStateHandle: SavedStateHandle): T

    protected abstract fun getParamsFromArgs(args: T): Map<String, Any>

    fun getRouteWithArgs(args: T): String {
        val params = getParamsFromArgs(args)
        return replaceParamsInRoute(params)
    }

    private fun replaceParamsInRoute(params: Map<String, Any>): String {
        var route = this.route
        params.forEach {
            route = route.replace("{${it.key}}", it.value.toString())
        }
        return route
    }

    protected fun StringBuilder.appendArguments() {
        arguments.filter {
            !it.argument.isNullable
        }.forEach {
            append("/{${it.name}}")
        }

        arguments.filter {
            it.argument.isNullable
        }.forEachIndexed { i: Int, argument: NamedNavArgument ->
            if (i == 0) {
                append("?${argument.name}={${argument.name}}")
            } else {
                append("$${argument.name}={${argument.name}}")
            }
        }
    }
}

open class NoArgDestination(baseRoute: String) :
    Destination<Nothing>(baseRoute) {

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun getParamsFromArgs(args: Nothing): Map<String, Any> {
        throw NoArgumentsException()
    }
}
