package dev.pott.abonity.navigation.destination

fun ArgumentDestination<*>.createDeeplinkRoute(
    host: String,
    domain: String,
    pathPostfix: String? = null,
) = buildString {
    append("$host$domain$baseRoute")
    pathPostfix?.let { append(it) }
    appendArguments(this@createDeeplinkRoute)
}
