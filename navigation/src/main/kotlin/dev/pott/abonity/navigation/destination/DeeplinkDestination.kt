package dev.pott.abonity.navigation.destination

import androidx.navigation.NavDeepLink
import kotlinx.collections.immutable.ImmutableList

interface DeeplinkDestination {
    val deeplinks: ImmutableList<NavDeepLink>
}
