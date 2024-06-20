package dev.pott.abonity.navigation.destination

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder

fun NavDeepLinkBuilder.setDestination(
    destination: Destination,
    arguments: Arguments?,
): NavDeepLinkBuilder = setDestination(destRoute = destination.route, arguments?.let { bundle(it) })

fun NavDeepLinkBuilder.setArguments(arguments: Arguments): NavDeepLinkBuilder {
    val bundle = bundle(arguments)
    return setArguments(bundle)
}

private fun bundle(arguments: Arguments): Bundle {
    val argArray = arguments.toMap().map { it.key to it.value }.toTypedArray()
    return bundleOf(*argArray)
}
