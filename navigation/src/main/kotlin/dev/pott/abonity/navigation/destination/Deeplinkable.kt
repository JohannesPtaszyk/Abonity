package dev.pott.abonity.navigation.destination

import androidx.navigation.NavDeepLink
import kotlinx.collections.immutable.ImmutableList

interface Deeplinkable {
    val deeplinks: ImmutableList<NavDeepLink>
}
