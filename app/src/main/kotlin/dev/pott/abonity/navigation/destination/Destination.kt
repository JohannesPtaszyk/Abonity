package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink

abstract class Destination<T>(val baseRoute: String) {

    open val route: String by lazy {
        buildString {
            append(baseRoute)
            appendArguments()
        }
    }

    open val deeplinks: List<NavDeepLink> = emptyList()

    abstract val arguments: List<NamedNavArgument>

    abstract fun getArgs(savedStateHandle: SavedStateHandle): T

    abstract fun routeWithArgs(args: T): String

    protected fun StringBuilder.appendArguments() {
        arguments.filter {
            !it.argument.isNullable
        }.forEach {
            append("/$it")
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

open class NoArgDestination(baseRoute: String): Destination<Nothing>(baseRoute) {

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun routeWithArgs(args: Nothing): String {
        throw NoArgumentsException()
    }
}