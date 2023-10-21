package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument

abstract class NavigationDestination<T>(
    baseRoute: String
) : Destination<T>(baseRoute) {
    abstract val startDestination: Destination<*>
}

abstract class NoArgNavigationDestination(
    baseRoute: String
) : NavigationDestination<Nothing>(baseRoute) {

    override val arguments: List<NamedNavArgument> = emptyList()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun getParamsFromArgs(args: Nothing): Map<String, Any> {
        throw NoArgumentsException()
    }
}
