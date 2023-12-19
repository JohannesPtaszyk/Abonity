package dev.pott.abonity.navigation.destination

import androidx.navigation.NamedNavArgument

internal fun StringBuilder.appendArguments(destination: ArgumentDestination<*>) {
    with(destination) {
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
