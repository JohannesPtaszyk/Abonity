package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument

abstract class NestedDestination<T>(
    parent: Destination<*>,
    baseRoute: String,
) : Destination<T>(baseRoute) {

    override val route: String by lazy {
        buildString {
            append("${parent.baseRoute}/$baseRoute")
            appendArguments()
        }
    }
}

open class NoArgNestedDestination(
    parent: Destination<*>,
    baseRoute: String,
) : NestedDestination<Nothing>(parent, baseRoute) {

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun getParamsFromArgs(args: Nothing): Map<String, Any> {
        throw NoArgumentsException()
    }
}
