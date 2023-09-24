package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument

abstract class NestedNavigationDestination<T>(
    parent: Destination<*>,
    baseRoute: String,
) : NavigationDestination<T>(baseRoute) {

    override val route: String = buildString {
        append("${parent.baseRoute}/$baseRoute")
        appendArguments()
    }
}

abstract class NoArgNestedNavigationDestination(
    parent: Destination<*>,
    baseRoute: String,
) : NestedNavigationDestination<Nothing>(parent, baseRoute) {

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun routeWithArgs(args: Nothing): String {
        throw NoArgumentsException()
    }
}