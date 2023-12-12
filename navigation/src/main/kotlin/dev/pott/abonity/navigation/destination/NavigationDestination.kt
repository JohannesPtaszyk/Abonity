package dev.pott.abonity.navigation.destination

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf

abstract class NavigationDestination<T>(
    baseRoute: String,
) : Destination<T>(baseRoute) {
    abstract val startDestination: Destination<*>
}

abstract class NoArgNavigationDestination(
    baseRoute: String,
) : NavigationDestination<Nothing>(baseRoute) {
    override val arguments: ImmutableList<NamedNavArgument> = persistentListOf()

    override fun getArgs(savedStateHandle: SavedStateHandle): Nothing {
        throw NoArgumentsException()
    }

    override fun getParamsFromArgs(args: Nothing): ImmutableMap<String, Any> {
        throw NoArgumentsException()
    }
}
