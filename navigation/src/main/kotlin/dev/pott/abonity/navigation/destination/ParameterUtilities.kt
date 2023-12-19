package dev.pott.abonity.navigation.destination

fun String.replaceNavArguments(arguments: Map<String, Any>): String {
    var route = this
    arguments.forEach {
        route = route.replace("{${it.key}}", it.value.toString())
    }
    return route
}

fun String.replaceNavArguments(arguments: Arguments): String {
    var route = this
    arguments.toMap().forEach {
        route = route.replace("{${it.key}}", it.value.toString())
    }
    return route
}
